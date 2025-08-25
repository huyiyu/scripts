package io.grpc.examples.alts;

import io.grpc.alts.AltsChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An example gRPC client that uses ALTS. Shows how to do a   Unary RPC. This example can only be run
 * on Google Cloud Platform.
 */
@Slf4j
public final class HelloWorldAltsClient {

    private String serverAddress = "localhost:10001";

    public static void main(String[] args) throws InterruptedException {
        new HelloWorldAltsClient().run(args);
    }

    private void parseArgs(String[] args) {
        boolean usage = false;
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                System.err.println("All arguments must start with '--': " + arg);
                usage = true;
                break;
            }
            String[] parts = arg.substring(2).split("=", 2);
            String key = parts[0];
            if ("help".equals(key)) {
                usage = true;
                break;
            }
            if (parts.length != 2) {
                System.err.println("All arguments must be of the form --arg=value");
                usage = true;
                break;
            }
            String value = parts[1];
            if ("server".equals(key)) {
                serverAddress = value;
            } else {
                System.err.println("Unknown argument: " + key);
                usage = true;
                break;
            }
        }
        if (usage) {
            HelloWorldAltsClient c = new HelloWorldAltsClient();
            System.out.println(
                "Usage: [ARGS...]"
                    + "\n"
                    + "\n  --server=SERVER_ADDRESS        Server address to connect to. Default "
                    + c.serverAddress);
            System.exit(1);
        }
    }

    private void run(String[] args) throws InterruptedException {
        parseArgs(args);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        ManagedChannel channel = Grpc.newChannelBuilder(serverAddress,
                AltsChannelCredentials.create())
            .executor(executor).build();
        try {
            GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
            HelloReply resp = stub.sayHello(HelloRequest.newBuilder().setName("Waldo").build());

            log.info("Got {}", resp);
        } finally {
            channel.shutdown();
            channel.awaitTermination(1, TimeUnit.SECONDS);
            // Wait until the channel has terminated, since tasks can be queued after the channel is
            // shutdown.
            executor.shutdown();
        }
    }
}