package com.iot.testformaffile.service;

import com.iot.testformaffile.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class ConsumerService {

    // 监听队列
    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE)
    public void receiveMessage(String message, Channel channel, Message messageObj) throws IOException {
        try {
            System.out.println("收到消息：" + message);

            // 手动确认消息（防止消息丢失）
            channel.basicAck(messageObj.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 消费失败，拒绝消息
            channel.basicNack(messageObj.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}