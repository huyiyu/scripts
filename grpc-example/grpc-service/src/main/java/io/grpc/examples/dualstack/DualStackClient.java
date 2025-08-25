package io.grpc.examples.dualstack;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A client that requests greetings from the {@link DualStackServer}.
 * First it sends 5 requests using the default nameresolver and load balancer.
 * Then it sends 10 requests using the example nameresolver and round robin load balancer.  These
 * requests are evenly distributed among the 3 servers rather than favoring the server listening
 * on both addresses because the ExampleDualStackNameResolver groups the 3 servers as 3 endpoints
 * each with 2 addresses.
 */
@Slf4j
public class DualStackClient {
    public static final String channelTarget = "example:///lb.example.grpc.io";
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public DualStackClient(Channel channel) {
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws Exception {
        NameResolverRegistry.getDefaultRegistry()
                .register(new ExampleDualStackNameResolverProvider());

        log.info(" **** Use default DNS resolver ****");
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50051")
                .usePlaintext()
                .build();
        try {
            DualStackClient client = new DualStackClient(channel);
            for (int i = 0; i < 5; i++) {
                client.greet("request:" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }

        log.info("**** Change to use example name resolver ****");
        /*
          Dial to "example:///resolver.example.grpc.io", use {@link ExampleNameResolver} to create connection
          "resolver.example.grpc.io" is converted to {@link java.net.URI.path}
         */
        channel = ManagedChannelBuilder.forTarget(channelTarget)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        try {
            DualStackClient client = new DualStackClient(channel);
            for (int i = 0; i < 10; i++) {
                client.greet("request:" + i);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public void greet(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            return;
        }
        log.info("Greeting: {}", response.getMessage());
    }
}
