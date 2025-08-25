package com.huyiyu.grpc.service;

import com.huyiyu.grpc.helloworld.HelloReply;
import com.huyiyu.grpc.helloworld.HelloRequest;

public interface IGreeting {
    HelloReply sayHello(HelloRequest helloRequest);
}
