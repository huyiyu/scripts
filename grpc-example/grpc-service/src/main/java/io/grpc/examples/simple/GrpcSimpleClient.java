package io.grpc.examples.simple;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcSimpleClient {

    public static void main(String[] args) throws StatusException {

        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        GreeterGrpc.GreeterBlockingV2Stub greeterBlockingV2Stub = GreeterGrpc.newBlockingV2Stub(channel);
        for (int i = 0; i < 20; i++) {
            HelloReply helloReply = greeterBlockingV2Stub.sayHello(HelloRequest.newBuilder().setName("simpleClient " + i).build());
            log.info("helloReply: {}", helloReply);
        }

    }
}
