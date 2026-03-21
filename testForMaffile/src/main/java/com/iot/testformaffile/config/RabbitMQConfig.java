package com.iot.testformaffile.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String TEST_QUEUE = "test_queue";

    // 创建队列
    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, true); // 持久化队列
    }
}