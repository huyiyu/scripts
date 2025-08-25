/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.examples.tls;

import io.grpc.Grpc;
import io.grpc.Server;
import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server with TLS enabled.
 */
@Slf4j
public class HelloWorldServerTls {

    private Server server;

    private final int port;
    private final ServerCredentials creds;

    public HelloWorldServerTls(int port, ServerCredentials creds) {
        this.port = port;
        this.creds = creds;
    }

    private void start() throws IOException {
        server = Grpc.newServerBuilderForPort(port, creds)
                .addService(new GreeterImpl())
                .build()
                .start();
        log.info("Server started, listening on {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                log.info("*** shutting down gRPC server since JVM is shutting down");
                HelloWorldServerTls.this.stop();
                log.info("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        try (InputStream ca = HelloWorldServerTls.class.getClassLoader().getResourceAsStream("tls/server/ca.pem");
             InputStream server = HelloWorldServerTls.class.getClassLoader().getResourceAsStream("tls/server/server.pem");
             InputStream key = HelloWorldServerTls.class.getClassLoader().getResourceAsStream("tls/server/server-key-pkcs8.pem")) {
            if (ca != null && server != null && key != null) {
                ServerCredentials serverCredentials = TlsServerCredentials
                        .newBuilder()
                        .keyManager(server,key)
                        .trustManager(ca)
                        .clientAuth(TlsServerCredentials.ClientAuth.REQUIRE)
                        .build();
                HelloWorldServerTls helloWorldServerTls = new HelloWorldServerTls(9999, serverCredentials);
                helloWorldServerTls.start();
                helloWorldServerTls.blockUntilShutdown();
            }
        }
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
