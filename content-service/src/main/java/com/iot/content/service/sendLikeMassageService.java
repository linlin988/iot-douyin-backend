package com.iot.content.service;

import com.iot.commonModules.DTO.LikeMessageDTO;
import com.iot.commonModules.config.RabbitMqConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class sendLikeMassageService {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public  void sendLikeMessage(LikeMessageDTO likeMessageDTO){
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.LIKE_EXCHANGE,
                    RabbitMqConfig.LIKE_ROUTING_KEY,
                    likeMessageDTO);
            log.info("发送点赞消息成功");
        } catch (Exception e) {
            log.error("发送点赞消息失败", e);
        }
    }
}
