package com.huyiyu.grpc.service.impl;

import com.huyiyu.grpc.helloworld.GreeterGrpc;
import com.huyiyu.grpc.helloworld.HelloReply;
import com.huyiyu.grpc.helloworld.HelloRequest;
import com.huyiyu.grpc.service.IGreeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceFacade extends GreeterGrpc.GreeterImplBase {

    private IGreeting greeting;

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply helloReply = greeting.sayHello(request);
        responseObserver.onNext(helloReply);
    }
}
