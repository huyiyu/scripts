package com.huyiyu.grpc.starter.property;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.Resource;
import org.springframework.util.unit.DataSize;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
@Data
public class GrpcServerProperties {

    /**
     * grpc 端口
     */
    private Integer port = 6565;
    /**
     * 是否启用 grpc
     */
    private boolean enabled = true;

    private SecurityProperties security;

    private RecoveryProperties recovery;

    private NettyServerProperties nettyServer;
    /**
     * Enables server reflection using <a href="https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md">ProtoReflectionService</a>.
     * Available only from gRPC 1.3 or higher.
     */
    private boolean enableReflection = false;

    /**
     * Number of seconds to wait for preexisting calls to finish before shutting down.
     * A negative value is equivalent to an infinite grace period
     */
    private int shutdownGrace = 0;

    }

    @Getter
    @Setter
    public static class RecoveryProperties {
        private Integer interceptorOrder;
    }

    @Getter
    @Setter
    public static class SecurityProperties {
        private Resource certChain;
        private Resource privateKey;
        private Auth auth;

        @Getter
        @Setter
        public static class Auth {
            private Integer interceptorOrder;
            private boolean failFast = true;
        }
    }


    @Getter
    @Setter
    public static class NettyServerProperties {
        private boolean onCollisionPreferShadedNetty;
        private Integer flowControlWindow;
        private Integer initialFlowControlWindow;

        private Integer maxConcurrentCallsPerConnection;

        private Duration keepAliveTime;
        private Duration keepAliveTimeout;

        private Duration maxConnectionAge;
        private Duration maxConnectionAgeGrace;
        private Duration maxConnectionIdle;
        private Duration permitKeepAliveTime;

        private DataSize maxInboundMessageSize;
        private DataSize maxInboundMetadataSize;

        private Boolean permitKeepAliveWithoutCalls;
        /**
         * grpc listen address. <P>If configured, takes precedence over {@code grpc.port} property value.</p>
         * Supported format:
         * <ul><li>{@code host:port} (if port is less than 1, uses random value)
         * <li>{@code host:}  (uses default grpc port, 6565 )</ul>
         */
        private InetSocketAddress primaryListenAddress;

        private List<InetSocketAddress> additionalListenAddresses;
    }

}
