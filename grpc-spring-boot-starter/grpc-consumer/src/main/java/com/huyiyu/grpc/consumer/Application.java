package com.huyiyu.grpc.consumer;

import com.huyiyu.grpc.helloworld.GreeterGrpc;
import com.huyiyu.grpc.helloworld.HelloReply;
import com.huyiyu.grpc.helloworld.HelloRequest;
import io.grpc.stub.AbstractStub;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.stubfactory.BlockingStubFactory;
import net.devh.boot.grpc.client.stubfactory.StandardJavaGrpcStubFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@Slf4j
public class Application {

    @GrpcClient("local-grpc-server")
    private GreeterGrpc.GreeterBlockingV2Stub greeterBlockingStub;



    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("greeting")
    public String greeting() {
        HelloReply huyiyu = greeterBlockingStub.sayHello(HelloRequest.newBuilder().setName("huyiyu").build());
        log.info(huyiyu.getMessage());
        return "ok";
    }


    @Bean
    public BlockingV2StubFactory blockingV2StubFactory(){
        return new BlockingV2StubFactory();
    }

    public static class BlockingV2StubFactory extends StandardJavaGrpcStubFactory implements PriorityOrdered {
        @Override
        protected String getFactoryMethodName() {
            return "newBlockingV2Stub";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractStub<?>> stubType) {
            return stubType.getCanonicalName().contains("v2");
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}
