package io.grpc.servlet;

import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Attributes;
import io.grpc.InternalLogId;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.internal.AbstractServerStream;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.SerializingExecutor;
import io.grpc.internal.StatsTraceContext;
import io.grpc.internal.TransportFrameUtil;
import io.grpc.internal.TransportTracer;
import io.grpc.internal.WritableBuffer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

final class ServletServerStream extends AbstractServerStream {
    private static final Logger logger = Logger.getLogger(ServletServerStream.class.getName());
    private final ServletTransportState transportState;
    private final Sink sink = new Sink();
    private final AsyncContext asyncCtx;
    private final HttpServletResponse resp;
    private final Attributes attributes;
    private final String authority;
    private final InternalLogId logId;
    private final AsyncServletOutputStreamWriter writer;

    ServletServerStream(AsyncContext asyncCtx, StatsTraceContext statsTraceCtx, int maxInboundMessageSize, Attributes attributes, String authority, InternalLogId logId) throws IOException {
        super(ByteArrayWritableBuffer::new, statsTraceCtx);
        this.transportState = new ServletTransportState(maxInboundMessageSize, statsTraceCtx, new TransportTracer());
        this.attributes = attributes;
        this.authority = authority;
        this.logId = logId;
        this.asyncCtx = asyncCtx;
        this.resp = (HttpServletResponse)asyncCtx.getResponse();
        this.writer = new AsyncServletOutputStreamWriter(asyncCtx, this.transportState, logId);
        this.resp.getOutputStream().setWriteListener(new GrpcWriteListener());
    }

    protected ServletTransportState transportState() {
        return this.transportState;
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public String getAuthority() {
        return this.authority;
    }

    public int streamId() {
        return -1;
    }

    protected Sink abstractServerStreamSink() {
        return this.sink;
    }

    private void writeHeadersToServletResponse(Metadata metadata) {
        metadata.discardAll(GrpcUtil.CONTENT_TYPE_KEY);
        metadata.discardAll(GrpcUtil.TE_HEADER);
        metadata.discardAll(GrpcUtil.USER_AGENT_KEY);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "[{0}] writeHeaders {1}", new Object[]{this.logId, metadata});
        }

        this.resp.setStatus(200);
        this.resp.setContentType("application/grpc");
        byte[][] serializedHeaders = TransportFrameUtil.toHttp2Headers(metadata);

        for(int i = 0; i < serializedHeaders.length; i += 2) {
            this.resp.addHeader(new String(serializedHeaders[i], StandardCharsets.US_ASCII), new String(serializedHeaders[i + 1], StandardCharsets.US_ASCII));
        }

    }

    static String toHexString(byte[] bytes, int length) {
        String hex = BaseEncoding.base16().encode(bytes, 0, Math.min(length, 64));
        if (length > 80) {
            hex = hex + "...";
        }

        if (length > 64) {
            int offset = Math.max(64, length - 16);
            hex = hex + BaseEncoding.base16().encode(bytes, offset, length - offset);
        }

        return hex;
    }

    final class ServletTransportState extends AbstractServerStream.TransportState {
        private final SerializingExecutor transportThreadExecutor;

        private ServletTransportState(int maxMessageSize, StatsTraceContext statsTraceCtx, TransportTracer transportTracer) {
            super(maxMessageSize, statsTraceCtx, transportTracer);
            this.transportThreadExecutor = new SerializingExecutor(MoreExecutors.directExecutor());
        }

        public void runOnTransportThread(Runnable r) {
            this.transportThreadExecutor.execute(r);
        }

        public void bytesRead(int numBytes) {
        }

        public void deframeFailed(Throwable cause) {
            if (ServletServerStream.logger.isLoggable(Level.WARNING)) {
                ServletServerStream.logger.log(Level.WARNING, String.format("[{%s}] Exception processing message", ServletServerStream.this.logId), cause);
            }

            ServletServerStream.this.cancel(Status.fromThrowable(cause));
        }
    }

    private static final class ByteArrayWritableBuffer implements WritableBuffer {
        private final int capacity;
        final byte[] bytes;
        private int index;

        ByteArrayWritableBuffer(int capacityHint) {
            this.bytes = new byte[Math.min(1048576, capacityHint)];
            this.capacity = this.bytes.length;
        }

        public void write(byte[] src, int srcIndex, int length) {
            System.arraycopy(src, srcIndex, this.bytes, this.index, length);
            this.index += length;
        }

        public void write(byte b) {
            this.bytes[this.index++] = b;
        }

        public int writableBytes() {
            return this.capacity - this.index;
        }

        public int readableBytes() {
            return this.index;
        }

        public void release() {
        }
    }

    private final class GrpcWriteListener implements WriteListener {
        private GrpcWriteListener() {
        }

        public void onError(Throwable t) {
            if (ServletServerStream.logger.isLoggable(Level.FINE)) {
                ServletServerStream.logger.log(Level.FINE, String.format("[{%s}] Error: ", ServletServerStream.this.logId), t);
            }

            if (!ServletServerStream.this.resp.isCommitted()) {
                ServletServerStream.this.cancel(Status.fromThrowable(t));
            } else {
                ServletServerStream.this.transportState.runOnTransportThread(() -> ServletServerStream.this.transportState.transportReportStatus(Status.fromThrowable(t)));
            }

        }

        public void onWritePossible() throws IOException {
            ServletServerStream.this.writer.onWritePossible();
        }
    }

    private final class Sink implements AbstractServerStream.Sink {
        final TrailerSupplier trailerSupplier;

        private Sink() {
            this.trailerSupplier = new TrailerSupplier();
        }

        public void writeHeaders(Metadata headers, boolean flush) {
            ServletServerStream.this.writeHeadersToServletResponse(headers);
            ServletServerStream.this.resp.setTrailerFields(this.trailerSupplier);

            try {
                ServletServerStream.this.writer.flush();
            } catch (IOException e) {
                ServletServerStream.logger.log(Level.WARNING, String.format("[{%s}] Exception when flushBuffer", ServletServerStream.this.logId), e);
                this.cancel(Status.fromThrowable(e));
            }

        }

        public void writeFrame(@Nullable WritableBuffer frame, boolean flush, int numMessages) {
            if (frame != null || flush) {
                if (ServletServerStream.logger.isLoggable(Level.FINEST)) {
                    ServletServerStream.logger.log(Level.FINEST, "[{0}] writeFrame: numBytes = {1}, flush = {2}, numMessages = {3}", new Object[]{ServletServerStream.this.logId, frame == null ? 0 : frame.readableBytes(), flush, numMessages});
                }

                try {
                    if (frame != null) {
                        int numBytes = frame.readableBytes();
                        if (numBytes > 0) {
                            ServletServerStream.this.onSendingBytes(numBytes);
                        }

                        ServletServerStream.this.writer.writeBytes(((ByteArrayWritableBuffer)frame).bytes, frame.readableBytes());
                    }

                    if (flush) {
                        ServletServerStream.this.writer.flush();
                    }
                } catch (IOException e) {
                    ServletServerStream.logger.log(Level.WARNING, String.format("[{%s}] Exception writing message", ServletServerStream.this.logId), e);
                    this.cancel(Status.fromThrowable(e));
                }

            }
        }

        public void writeTrailers(Metadata trailers, boolean headersSent, Status status) {
            if (ServletServerStream.logger.isLoggable(Level.FINE)) {
                ServletServerStream.logger.log(Level.FINE, "[{0}] writeTrailers: {1}, headersSent = {2}, status = {3}", new Object[]{ServletServerStream.this.logId, trailers, headersSent, status});
            }

            if (!headersSent) {
                ServletServerStream.this.writeHeadersToServletResponse(trailers);
            } else {
                byte[][] serializedHeaders = TransportFrameUtil.toHttp2Headers(trailers);

                for(int i = 0; i < serializedHeaders.length; i += 2) {
                    String key = new String(serializedHeaders[i], StandardCharsets.US_ASCII);
                    String newValue = new String(serializedHeaders[i + 1], StandardCharsets.US_ASCII);
                    this.trailerSupplier.get().computeIfPresent(key, (k, v) -> v + "," + newValue);
                    this.trailerSupplier.get().putIfAbsent(key, newValue);
                }
            }

            ServletServerStream.this.writer.complete();
        }

        public void cancel(Status status) {
            if (!ServletServerStream.this.resp.isCommitted() || Code.DEADLINE_EXCEEDED != status.getCode()) {
                ServletServerStream.this.transportState.runOnTransportThread(() -> ServletServerStream.this.transportState.transportReportStatus(status));
                ServletServerStream.this.close(Status.CANCELLED.withDescription("Servlet stream cancelled").withCause(status.asRuntimeException()), new Metadata());
                CountDownLatch countDownLatch = new CountDownLatch(1);
                ServletServerStream.this.transportState.runOnTransportThread(() -> {
                    ServletServerStream.this.asyncCtx.complete();
                    countDownLatch.countDown();
                });

                try {
                    countDownLatch.await(5L, TimeUnit.SECONDS);
                } catch (InterruptedException var4) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }

    private static final class TrailerSupplier implements Supplier<Map<String, String>> {
        final Map<String, String> trailers = Collections.synchronizedMap(new HashMap());

        TrailerSupplier() {
        }

        public Map<String, String> get() {
            return this.trailers;
        }
    }
}
