package com.huyiyu.rocketmqv4;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Rocketmqv4ProducerApplication {

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Value("${spring.application.name}")
    private String appName;


    public static void main(String[] args) {
        SpringApplication.run(Rocketmqv4ProducerApplication.class, args);
    }


    @GetMapping("send")
    public String sendMessage(){
        rocketMQTemplate.convertAndSend("ROCKETMQ-V5-TEST",appName+":send");
        return "OK";
    }
}
