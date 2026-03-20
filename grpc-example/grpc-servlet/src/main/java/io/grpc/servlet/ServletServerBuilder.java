//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.grpc.servlet;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.Attributes;
import io.grpc.ExperimentalApi;
import io.grpc.ForwardingServerBuilder;
import io.grpc.Internal;
import io.grpc.InternalChannelz;
import io.grpc.InternalInstrumented;
import io.grpc.InternalLogId;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.InternalServer;
import io.grpc.internal.ServerImplBuilder;
import io.grpc.internal.ServerListener;
import io.grpc.internal.ServerStream;
import io.grpc.internal.ServerTransport;
import io.grpc.internal.ServerTransportListener;
import io.grpc.internal.SharedResourceHolder;
import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

@ExperimentalApi("https://github.com/grpc/grpc-java/issues/5066")
@NotThreadSafe
public final class ServletServerBuilder extends ForwardingServerBuilder<ServletServerBuilder> {
    List<? extends ServerStreamTracer.Factory> streamTracerFactories;
    int maxInboundMessageSize = 4194304;
    private final ServerImplBuilder serverImplBuilder = new ServerImplBuilder(this::buildTransportServers);
    private ScheduledExecutorService scheduler;
    private boolean internalCaller;
    private boolean usingCustomScheduler;
    private InternalServerImpl internalServer;

    public Server build() {
        Preconditions.checkState(this.internalCaller, "build() method should not be called directly by an application");
        return super.build();
    }

    public ServletAdapter buildServletAdapter() {
        return new ServletAdapter(this.buildAndStart(), this.streamTracerFactories, this.maxInboundMessageSize);
    }

    public GrpcServlet buildServlet() {
        return new GrpcServlet(this.buildServletAdapter());
    }

    private ServerTransportListener buildAndStart() {
        final Server server;
        try {
            this.internalCaller = true;
            server = this.build().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.internalCaller = false;
        }

        if (!this.usingCustomScheduler) {
            this.scheduler = (ScheduledExecutorService)SharedResourceHolder.get(GrpcUtil.TIMER_SERVICE);
        }

        ServerTransportImpl serverTransport = new ServerTransportImpl(this.scheduler);
        final ServerTransportListener delegate = this.internalServer.serverListener.transportCreated(serverTransport);
        return new ServerTransportListener() {
            public void streamCreated(ServerStream stream, String method, Metadata headers) {
                delegate.streamCreated(stream, method, headers);
            }

            public Attributes transportReady(Attributes attributes) {
                return delegate.transportReady(attributes);
            }

            public void transportTerminated() {
                server.shutdown();
                delegate.transportTerminated();
                if (!ServletServerBuilder.this.usingCustomScheduler) {
                    SharedResourceHolder.release(GrpcUtil.TIMER_SERVICE, ServletServerBuilder.this.scheduler);
                }

            }
        };
    }

    @VisibleForTesting
    InternalServer buildTransportServers(List<? extends ServerStreamTracer.Factory> streamTracerFactories) {
        Preconditions.checkNotNull(streamTracerFactories, "streamTracerFactories");
        this.streamTracerFactories = streamTracerFactories;
        this.internalServer = new InternalServerImpl();
        return this.internalServer;
    }

    @Internal
    protected ServerBuilder<?> delegate() {
        return this.serverImplBuilder;
    }

    public ServletServerBuilder useTransportSecurity(File certChain, File privateKey) {
        throw new UnsupportedOperationException("TLS should be configured by the servlet container");
    }

    public ServletServerBuilder maxInboundMessageSize(int bytes) {
        Preconditions.checkArgument(bytes >= 0, "bytes must be >= 0");
        this.maxInboundMessageSize = bytes;
        return this;
    }

    public ServletServerBuilder scheduledExecutorService(ScheduledExecutorService scheduler) {
        this.scheduler = (ScheduledExecutorService)Preconditions.checkNotNull(scheduler, "scheduler");
        this.usingCustomScheduler = true;
        return this;
    }

    private static final class InternalServerImpl implements InternalServer {
        ServerListener serverListener;

        InternalServerImpl() {
        }

        public void start(ServerListener listener) {
            this.serverListener = listener;
        }

        public void shutdown() {
            if (this.serverListener != null) {
                this.serverListener.serverShutdown();
            }

        }

        public SocketAddress getListenSocketAddress() {
            return new SocketAddress() {
                public String toString() {
                    return "ServletServer";
                }
            };
        }

        public InternalInstrumented<InternalChannelz.SocketStats> getListenSocketStats() {
            return null;
        }

        public List<? extends SocketAddress> getListenSocketAddresses() {
            return Collections.emptyList();
        }

        @Nullable
        public List<InternalInstrumented<InternalChannelz.SocketStats>> getListenSocketStatsList() {
            return null;
        }
    }

    @VisibleForTesting
    static final class ServerTransportImpl implements ServerTransport {
        private final InternalLogId logId = InternalLogId.allocate(ServerTransportImpl.class, (String)null);
        private final ScheduledExecutorService scheduler;

        ServerTransportImpl(ScheduledExecutorService scheduler) {
            this.scheduler = (ScheduledExecutorService)Preconditions.checkNotNull(scheduler, "scheduler");
        }

        public void shutdown() {
        }

        public void shutdownNow(Status reason) {
        }

        public ScheduledExecutorService getScheduledExecutorService() {
            return this.scheduler;
        }

        public ListenableFuture<InternalChannelz.SocketStats> getStats() {
            return null;
        }

        public InternalLogId getLogId() {
            return this.logId;
        }
    }
}
