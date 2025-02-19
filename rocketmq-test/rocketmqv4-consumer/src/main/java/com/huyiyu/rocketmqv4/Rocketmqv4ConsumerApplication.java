package com.huyiyu.rocketmqv4;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Rocketmqv4ConsumerApplication {


    public static void main(String[] args) {
        SpringApplication.run(Rocketmqv4ConsumerApplication.class, args);
    }

}
