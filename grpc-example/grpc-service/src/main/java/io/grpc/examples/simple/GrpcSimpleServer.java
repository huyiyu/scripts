package io.grpc.examples.simple;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.examples.debug.HostnameGreeter;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.reflection.ReflectionServer;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import io.grpc.services.AdminInterface;

import java.io.IOException;

public class GrpcSimpleServer {

    private static final int SERVER_PORT = 50051;
    private static Server GRPC_SERVER;


    public static void main(String[] args) throws IOException, InterruptedException {
        GRPC_SERVER = Grpc.newServerBuilderForPort(SERVER_PORT, InsecureServerCredentials.create())
                .addService(new SimpleGreeter())
                .addService(ProtoReflectionServiceV1.newInstance())
                .addServices(AdminInterface.getStandardServices())
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(GrpcSimpleServer::shutdown, "shutdownThread"));
        GRPC_SERVER.start().awaitTermination();
    }

    private static void shutdown() {
        try {
            GRPC_SERVER.shutdown();
            GRPC_SERVER.awaitTermination();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
