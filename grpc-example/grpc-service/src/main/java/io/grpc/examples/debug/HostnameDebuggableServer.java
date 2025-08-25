package io.grpc.examples.debug;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import io.grpc.services.AdminInterface;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A server that hosts HostnameGreeter, plus the channelz service which grpcdebug uses.
 */
@Slf4j
public final class HostnameDebuggableServer {
    static int port = 50051;
    static String hostname = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        parseArgs(args); // sets port and hostname

        final Server server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new HostnameGreeter(hostname))
                .addServices(AdminInterface.getStandardServices()) // the key add for enabling grpcdebug
                .addService(ProtoReflectionServiceV1.newInstance())
                .build()
                .start();

        System.out.println("Listening on port " + port);

        addShutdownHook(server); // Configures cleanup
        server.awaitTermination();  // Block until shutdown
    }

    private static void parseArgs(String[] args) {
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.err.println("Usage: [port [hostname]]");
                System.err.println("");
                System.err.println("  port      The listen port. Defaults to " + port);
                System.err.println("  hostname  The name clients will see in greet responses. ");
                System.err.println("            Defaults to the machine's hostname");
                System.exit(1);
            }
        }
        if (args.length >= 2) {
            hostname = args[1];
        }
    }

    private static void addShutdownHook(final Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Start graceful shutdown
                server.shutdown();
                try {
                    // Wait for RPCs to complete processing
                    if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                        // That was plenty of time. Let's cancel the remaining RPCs
                        server.shutdownNow();
                        // shutdownNow isn't instantaneous, so give a bit of time to clean resources up
                        // gracefully. Normally this will be well under a second.
                        server.awaitTermination(5, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException ex) {
                    server.shutdownNow();
                }
            }
        });
    }
}