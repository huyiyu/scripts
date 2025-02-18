package com.huyiyu.rocketmqv5;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Rocketmqv5ProducerApplication {

    @Resource
    private RocketMQClientTemplate rocketMQClientTemplate;

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(Rocketmqv5ProducerApplication.class, args);
    }


    @GetMapping("send")
    public String sendMessage(){
        rocketMQClientTemplate.convertAndSend("rocketmq-v5-test",appName+":send");
        return "OK";
    }

}
