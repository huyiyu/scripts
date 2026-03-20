//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.grpc.servlet;

import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import io.grpc.Attributes;
import io.grpc.ExperimentalApi;
import io.grpc.Grpc;
import io.grpc.InternalLogId;
import io.grpc.InternalMetadata;
import io.grpc.Metadata;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.ReadableBuffers;
import io.grpc.internal.ServerTransportListener;
import io.grpc.internal.StatsTraceContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExperimentalApi("https://github.com/grpc/grpc-java/issues/5066")
public final class ServletAdapter {
    static final Logger logger = Logger.getLogger(ServletAdapter.class.getName());
    private final ServerTransportListener transportListener;
    private final List<? extends ServerStreamTracer.Factory> streamTracerFactories;
    private final int maxInboundMessageSize;
    private final Attributes attributes;

    ServletAdapter(ServerTransportListener transportListener, List<? extends ServerStreamTracer.Factory> streamTracerFactories, int maxInboundMessageSize) {
        this.transportListener = transportListener;
        this.streamTracerFactories = streamTracerFactories;
        this.maxInboundMessageSize = maxInboundMessageSize;
        this.attributes = transportListener.transportReady(Attributes.EMPTY);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(405, "GET method not supported");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Preconditions.checkArgument(req.isAsyncSupported(), "servlet does not support asynchronous operation");
        Preconditions.checkArgument(isGrpc(req), "the request is not a gRPC request");
        InternalLogId logId = InternalLogId.allocate(ServletAdapter.class, (String)null);
        logger.log(Level.FINE, "[{0}] RPC started", logId);
        AsyncContext asyncCtx = req.startAsync(req, resp);
        String method = req.getRequestURI().substring(1);
        Metadata headers = getHeaders(req);
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "[{0}] method: {1}", new Object[]{logId, method});
            logger.log(Level.FINEST, "[{0}] headers: {1}", new Object[]{logId, headers});
        }

        Long timeoutNanos = (Long)headers.get(GrpcUtil.TIMEOUT_KEY);
        if (timeoutNanos == null) {
            timeoutNanos = 0L;
        }

        asyncCtx.setTimeout(TimeUnit.NANOSECONDS.toMillis(timeoutNanos));
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(this.streamTracerFactories, method, headers);
        ServletServerStream stream = new ServletServerStream(asyncCtx, statsTraceCtx, this.maxInboundMessageSize, this.attributes.toBuilder().set(Grpc.TRANSPORT_ATTR_REMOTE_ADDR, new InetSocketAddress(req.getRemoteHost(), req.getRemotePort())).set(Grpc.TRANSPORT_ATTR_LOCAL_ADDR, new InetSocketAddress(req.getLocalAddr(), req.getLocalPort())).build(), getAuthority(req), logId);
        this.transportListener.streamCreated(stream, method, headers);
        ServletServerStream.ServletTransportState var10000 = stream.transportState();
        ServletServerStream.ServletTransportState var10001 = stream.transportState();
        Objects.requireNonNull(var10001);
        var10000.runOnTransportThread(() -> var10001.onStreamAllocated());
        asyncCtx.getRequest().getInputStream().setReadListener(new GrpcReadListener(stream, asyncCtx, logId));
        asyncCtx.addListener(new GrpcAsyncListener(stream, logId));
    }

    private static Metadata getHeaders(HttpServletRequest req) {
        Enumeration<String> headerNames = req.getHeaderNames();
        Preconditions.checkNotNull(headerNames, "Servlet container does not allow HttpServletRequest.getHeaderNames()");
        List<byte[]> byteArrays = new ArrayList();

        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            Enumeration<String> values = req.getHeaders(headerName);
            if (values != null) {
                while(values.hasMoreElements()) {
                    String value = (String)values.nextElement();
                    if (headerName.endsWith("-bin")) {
                        byteArrays.add(headerName.getBytes(StandardCharsets.US_ASCII));
                        byteArrays.add(BaseEncoding.base64().decode(value));
                    } else {
                        byteArrays.add(headerName.getBytes(StandardCharsets.US_ASCII));
                        byteArrays.add(value.getBytes(StandardCharsets.US_ASCII));
                    }
                }
            }
        }

        return InternalMetadata.newMetadata((byte[][])byteArrays.toArray(new byte[0][]));
    }

    private static String getAuthority(HttpServletRequest req) {
        try {
            return (new URI(req.getRequestURL().toString())).getAuthority();
        } catch (URISyntaxException var2) {
            logger.log(Level.FINE, "Error getting authority from the request URL {0}", req.getRequestURL());
            return req.getServerName() + ":" + req.getServerPort();
        }
    }

    public void destroy() {
        this.transportListener.transportTerminated();
    }

    public static boolean isGrpc(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().contains("application/grpc");
    }

    private static final class GrpcAsyncListener implements AsyncListener {
        final InternalLogId logId;
        final ServletServerStream stream;

        GrpcAsyncListener(ServletServerStream stream, InternalLogId logId) {
            this.stream = stream;
            this.logId = logId;
        }

        public void onComplete(AsyncEvent event) {
        }

        public void onTimeout(AsyncEvent event) {
            if (ServletAdapter.logger.isLoggable(Level.FINE)) {
                ServletAdapter.logger.log(Level.FINE, String.format("[{%s}] Timeout: ", this.logId), event.getThrowable());
            }

            if (!event.getAsyncContext().getResponse().isCommitted()) {
                this.stream.cancel(Status.DEADLINE_EXCEEDED);
            } else {
                this.stream.transportState().runOnTransportThread(() -> this.stream.transportState().transportReportStatus(Status.DEADLINE_EXCEEDED));
            }

        }

        public void onError(AsyncEvent event) {
            if (ServletAdapter.logger.isLoggable(Level.FINE)) {
                ServletAdapter.logger.log(Level.FINE, String.format("[{%s}] Error: ", this.logId), event.getThrowable());
            }

            if (!event.getAsyncContext().getResponse().isCommitted()) {
                this.stream.cancel(Status.fromThrowable(event.getThrowable()));
            } else {
                this.stream.transportState().runOnTransportThread(() -> this.stream.transportState().transportReportStatus(Status.fromThrowable(event.getThrowable())));
            }

        }

        public void onStartAsync(AsyncEvent event) {
        }
    }

    private static final class GrpcReadListener implements ReadListener {
        final ServletServerStream stream;
        final AsyncContext asyncCtx;
        final ServletInputStream input;
        final InternalLogId logId;
        final byte[] buffer = new byte[4096];

        GrpcReadListener(ServletServerStream stream, AsyncContext asyncCtx, InternalLogId logId) throws IOException {
            this.stream = stream;
            this.asyncCtx = asyncCtx;
            this.input = asyncCtx.getRequest().getInputStream();
            this.logId = logId;
        }

        public void onDataAvailable() throws IOException {
            ServletAdapter.logger.log(Level.FINEST, "[{0}] onDataAvailable: ENTRY", this.logId);

            while(this.input.isReady()) {
                int length = this.input.read(this.buffer);
                if (length == -1) {
                    ServletAdapter.logger.log(Level.FINEST, "[{0}] inbound data: read end of stream", this.logId);
                    return;
                }

                if (ServletAdapter.logger.isLoggable(Level.FINEST)) {
                    ServletAdapter.logger.log(Level.FINEST, "[{0}] inbound data: length = {1}, bytes = {2}", new Object[]{this.logId, length, ServletServerStream.toHexString(this.buffer, length)});
                }

                byte[] copy = Arrays.copyOf(this.buffer, length);
                this.stream.transportState().runOnTransportThread(() -> this.stream.transportState().inboundDataReceived(ReadableBuffers.wrap(copy), false));
            }

            ServletAdapter.logger.log(Level.FINEST, "[{0}] onDataAvailable: EXIT", this.logId);
        }

        public void onAllDataRead() {
            ServletAdapter.logger.log(Level.FINE, "[{0}] onAllDataRead", this.logId);
            this.stream.transportState().runOnTransportThread(() -> this.stream.transportState().inboundDataReceived(ReadableBuffers.empty(), true));
        }

        public void onError(Throwable t) {
            if (ServletAdapter.logger.isLoggable(Level.FINE)) {
                ServletAdapter.logger.log(Level.FINE, String.format("[{%s}] Error: ", this.logId), t);
            }

            if (!this.asyncCtx.getResponse().isCommitted()) {
                this.stream.cancel(Status.fromThrowable(t));
            } else {
                this.stream.transportState().runOnTransportThread(() -> this.stream.transportState().transportReportStatus(Status.fromThrowable(t)));
            }

        }
    }
}
