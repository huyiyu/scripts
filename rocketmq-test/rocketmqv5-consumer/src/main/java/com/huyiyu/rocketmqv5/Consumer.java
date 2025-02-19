package com.huyiyu.rocketmqv5;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@RocketMQMessageListener(topic = "ROCKETMQ-V5-TEST", consumerGroup = "rocketmq-v5",tag = "*")
@Component
public class Consumer implements RocketMQListener {


    @Value("${spring.application.name}")
    private String appName;


    @Override
    public ConsumeResult consume(MessageView messageView) {
        ByteBuffer byteBuffer = messageView.getBody();
        int capacity = byteBuffer.capacity();
        byte[] bytes = new byte[capacity];
        byteBuffer.get(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        log.info("{}:成功消费消息,msgId:{}:body:{}", appName, messageView.getMessageId(), body);
        return ConsumeResult.SUCCESS;
    }
}
