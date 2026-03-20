package io.grpc.examples.simple;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleGreeter extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("simple server run: {}", request);
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello, Simple" + request.getName()).build());
        responseObserver.onCompleted();
    }
}
