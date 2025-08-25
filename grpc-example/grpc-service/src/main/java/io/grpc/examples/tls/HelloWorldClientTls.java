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

import io.grpc.*;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServerTls} with TLS.
 */
@Slf4j
public class HelloWorldClientTls {
    private static final Logger logger = Logger.getLogger(HelloWorldClientTls.class.getName());

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public HelloWorldClientTls(Channel channel) {
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    /**
     * Say hello to server.
     */
    public void greet(String name) {
        log.info("TLS Will try to greet {} ...", name);
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

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        try (InputStream ca = HelloWorldServerTls.class.getClassLoader().getResourceAsStream("tls/client/ca.pem");
             InputStream client = HelloWorldServerTls.class.getClassLoader().getResourceAsStream("tls/client/client.pem");
             InputStream clientKey = HelloWorldServerTls.class.getClassLoader().getResourceAsStream("tls/client/client-key-pkcs8.pem")) {
            if (ca != null && client != null && clientKey != null) {
                ChannelCredentials channelCredentials = TlsChannelCredentials.newBuilder()
                        .keyManager(client, clientKey)
                        .trustManager(ca)
                        .build();
                ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 9999, channelCredentials)
                        .build();
                HelloWorldClientTls helloWorldClientTls = new HelloWorldClientTls(channel);
                for (int i = 0; i < 10; i++) {
                    helloWorldClientTls.greet("Client TLS");
                }
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }
}
