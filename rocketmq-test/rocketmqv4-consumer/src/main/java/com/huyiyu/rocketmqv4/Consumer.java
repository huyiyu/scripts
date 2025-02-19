package com.huyiyu.rocketmqv4;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@RocketMQMessageListener(topic = "ROCKETMQ-V5-TEST", consumerGroup = "rocketmq-v5")
@Component
public class Consumer implements RocketMQListener<MessageExt> {


    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("{}:成功消费消息,msgId:{}:body:{}", appName,messageExt.getMsgId(),new String(messageExt.getBody(),StandardCharsets.UTF_8));
    }
}
