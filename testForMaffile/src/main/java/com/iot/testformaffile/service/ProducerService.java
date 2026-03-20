package com.iot.testformaffile.service;

import com.iot.testformaffile.config.RabbitMQConfig;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    // 发送消息
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.TEST_QUEUE, message);
        System.out.println("发送成功：" + message);
    }
}