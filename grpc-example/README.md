# grpc 学习

## 1.grpc 概述

### 1.1 grpc 基础概念

> 原文: gRPC is a modern open source high performance Remote Procedure Call (RPC) framework that can run in any
> environment. It can efficiently connect services in and across data centers with pluggable support for load balancing,
> tracing, health checking and authentication. It is also applicable in last mile of distributed computing to connect
> devices, mobile applications and browsers to backend services.


> 译文（来自DeepSeek）：gRPC
>
是一种现代开源高性能远程过程调用（RPC）框架，可在任何环境中运行。它能够高效连接数据中心内外的服务，提供可插拔的负载均衡、链路追踪、健康检查及身份验证功能。该框架同样适用于分布式计算的最后一公里场景，可将物联网设备、移动应用程序和浏览器连接到后端服务。

### 1.2 grpc 优点

1. 简单服务定义
2. 高性能，学习成本低
3. 跨语言，跨平台
4. HTTP/2 双向传输，插件式身份验证。

---

### 1.3 grpc 的几个概念

- **stub** : 客户端,一般会有 blockingStub(阻塞型客户端),BlockV2Stub(阻塞型客户端,错误处理更友好),Stub(非阻塞型客户端)
  ,listenableFutureStub(基于回调的非阻塞客户端)可扩展plugin生成其他的Stub（如webFlux）。
- **service** : 服务端,一般是一个基础类,需要用户补充具体的用户实现。
- **channel** : 通道,顾名思义客户端访问服务端的桥梁,stub 通过 channel 和 service 进行通信。
- **NameResolver**: 域名解析器, channel 内部用于将 target 域名解析成具体 IP 地址的实现。
- **Loadbalancer**: 负载均衡器, channel 内部用户选择解析结果内容进行服务选择。
- **Credentials**: 认证证书,一般用于解决客户端和服务端认证问题,常用的有 Tls,Alts,Insecure,Jwt,以及各种自定义实现,服务端和客户端分别通过实现
  ServerCredentials 和 ChannelCredentials 来完成
- **Interceptor**: 拦截器,分为客户端拦截器（ClientInterceptor）和服务端拦截器(ServerInterceptor),用于在grpc调用的生命周期内提供逻辑织入

## 2. Grpc快速开始

### 2.1 定义调用规范

- **定义一个 protobuf 描述文件**： 由于grpc 是跨平台协议,需要一个中立的DSL语言(protobuf)来实现类似Java Interface 的效果,

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "io.grpc.examples.helloworld";
option java_outer_classname = "HelloWorldProto";
option java_string_check_utf8 = true;

package helloworld;

// The greeting service definition.
service Greeter {
  // Sends a greeting
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
  string name = 1;
}

// The response message containing the greetings
message HelloReply {
  string message = 1;
}
```

> 对于 protobuf , 可以参照官方文档: https://protobuf.dev/ 如图定义了一个 Service `Greeter` ,并定义了一个方法 `SayHello`
> 入参是 `HelloRequest` 返回值 `HelloReply`其中 HelloRequest 有一个字段,我们可以看出 Protobuf 之所以比
> json序列化要小是主要原因是它通过序号方式序列化属性,这一方面缩短了body 的表现,另一方面保证了接口的扩展性,往后迭代不至于因为body的变化序列化失败

### 2.2 生成 Java 代码。

根据官方文档,我们可以通过 `protoc` 工具生成对应不同语言的代码模板,以下提供两种方案

- 通过原生 protoc 命令生成

```bash
# protoc 的插件体系用于扩展生成代码体系,grpc 适配了 protoc 插件体系生成了GRPC Service 客户端和服务端代码
protoc --plugin=/c/soft/binarys/protoc-gen-grpc-java \
 --grpc-java_out=. \ 
 --java_out=. \
 [proto file]
```

- 通过编译工具生成
  如果使用编译工具辅助可参照官方文档, grpc-api 模块提供了gradle 中 protoc 插件的相关配置以生成grpc 服务端和客户端代码。

```groovy
protobuf {
    // 使用protoc 的具体版本
    protoc {
        artifact = "com.google.protobuf:protoc:4.31.1"
    }
    plugins {
        // protoc 插件体系 
        grpc {
            // grpc插件
            artifact = "io.grpc:protoc-gen-grpc-java:1.75.0"
        }
        reactor {
            // 第三方插件,用于支持使用 reactor API 生成 grpcStub (WebFlux 支持)
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.4"
        }
    }
    generateProtoTasks {
        ofSourceSet("main")*.plugins {
            grpc {
            }
            reactor {
            }
        }
    }
}
```

### 2.3 编写服务端实现

- 从生成代码中实现 GreeterGrpc.GreeterSer

```java
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
        // 此处需要编写业务逻辑,描述如何将Request 转化为Reply
        log.info("simple server run: {}", request);
        // 使用Observer.onNext 返回 固定的模板代码
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello, Simple" + request.getName()).build());
        // 模板代码,表示请求结束此时数据才能正常返回给客户端
        responseObserver.onCompleted();
    }
}
```

- 启动Server 进程并添加 SimpleGreeter 服务。

```java
package io.grpc.examples.simple;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.examples.debug.HostnameGreeter;
import io.grpc.examples.helloworld.GreeterGrpc;

import java.io.IOException;

public class GrpcSimpleServer {

    private static final int SERVER_PORT = 50051;
    private static Server GRPC_SERVER;


    public static void main(String[] args) throws IOException, InterruptedException {
        // 一行代码启动一个Grpc服务器,并添加Service,
        // InsecureServerCredentials 一般用于测试环境 表示不校验证书有效性 
        GRPC_SERVER = Grpc.newServerBuilderForPort(SERVER_PORT, InsecureServerCredentials.create())
                .addService(new SimpleGreeter())
                .build();
        // 通过使用ShutdownHook 来监听Java 程序退出,常见优雅关闭方案
        Runtime.getRuntime().addShutdownHook(new Thread(GrpcSimpleServer::shutdown, "shutdownThread"));
        // 启动进程并阻塞
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
```

### 2.4 编写客户端调用

```java
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
```

## 3. 进阶操作

### 3.1 debug方法

#### 3.1.1 反射支持
> grpc调用通常需要将对象序列成 protobuf 格式进行传输,如果开启了反射支持则可直接通过命令工具`grpcurl`扫描接口,并且无需使用 proto 规范定义即可直接通信,仅需要添加该 service `ProtoReflectionServiceV1.newInstance()`






#### 3.1.2 元信息获取
> 通过为Server 进程添加特殊的 Service 来提供类似 Spring Actuator 的健康检查以及监控端点的效果,测试代码如下

### 3.2 grpc 凭据选择

#### 3.2.1 TLS 凭据
> 熟悉 Tls 相关规范的同事应该知道,许多中间件(etcd,redis)都支持双向加密的证书通信（mTls）,Grpc 提供
#### 3.2.2  JWT 凭据
#### 3.2.3 自定义凭据
#### 3.2.3 几种凭据对象分析

### 3.3 负载均衡
### 3.4 域名解析


## 4. 源码分析
### 4.1 服务端如何启动
### 4.2 客户端如何启动
### 4.3 服务端和客户端通信过程中的生命周期