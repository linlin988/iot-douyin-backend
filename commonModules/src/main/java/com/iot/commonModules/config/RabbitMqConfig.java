package com.iot.commonModules.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String LIKE_EXCHANGE = "like.exchange";
    public static final String LIKE_QUEUE = "like.queue";
    public static final String LIKE_ROUTING_KEY = "like.notify";

    // 点赞交换机
    @Bean
    public DirectExchange likeExchange() {
        return new DirectExchange(LIKE_EXCHANGE, true, false);
    }

    // 点赞队列
    @Bean
    public Queue likeQueue() {
        return new Queue(LIKE_QUEUE, true);
    }

    // 点赞绑定
    @Bean
    public Binding likeBinding() {
        return BindingBuilder.bind(likeQueue())
                .to(likeExchange())
                .with(LIKE_ROUTING_KEY);
    }
    // 统一用Jackson2JsonMessageConverter，解决序列化/反序列化问题
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}