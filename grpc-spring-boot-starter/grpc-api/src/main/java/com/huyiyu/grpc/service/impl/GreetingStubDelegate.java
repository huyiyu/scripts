package com.huyiyu.grpc.service.impl;

import com.huyiyu.grpc.helloworld.GreeterGrpc;
import com.huyiyu.grpc.helloworld.HelloReply;
import com.huyiyu.grpc.helloworld.HelloRequest;
import com.huyiyu.grpc.service.IGreeting;
import io.grpc.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class GreetingStubDelegate implements InvocationHandler {

    private GreeterGrpc.GreeterBlockingV2Stub stub = GreeterGrpc.newBlockingStub(channel);
    private Channel channel;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isDefault()|| method.is)


        return null;
    }
}
