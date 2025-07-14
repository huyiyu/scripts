package com.huyiyu.grpc.producer.service;

import com.huyiyu.grpc.greeting.GreeterGrpc;
import com.huyiyu.grpc.greeting.HelloReply;
import com.huyiyu.grpc.greeting.HelloRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class GreetServiceImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("server say hello");
        HelloReply build = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();

    }
}
