package com.iot.UserService.Service;


import com.iot.commonModules.config.RabbitMqConfig;
import com.iot.commonModules.service.WebSocketServer;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.iot.commonModules.config.RabbitMqConfig.LIKE_QUEUE;
import com.iot.commonModules.DTO.LikeMessageDTO;

@Component
@Slf4j
public class LikeNotifyListener {

    @RabbitListener(queues = LIKE_QUEUE)
    public void handleLikeNotify(LikeMessageDTO dto, Channel channel, Message message) {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("收到点赞消息：{}", dto);

            // 检查消息内容是否为空
            if (dto == null) {
                log.warn("收到空的消息内容，deliveryTag: {}", deliveryTag);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 作者ID（接收通知的人）
            Long authorId = dto.getAuthorId();
            Long userId = dto.getUserId();

            if (authorId == null || userId == null) {
                log.warn("消息内容不完整，authorId: {}, userId: {}, deliveryTag: {}", authorId, userId, deliveryTag);
                channel.basicAck(deliveryTag, false);
                return;
            }

            String msg = "用户" + userId + "赞了你的视频";

            // 通过WebSocket推送给前端
            WebSocketServer.sendLikeNotify(authorId, msg);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            log.error("处理点赞通知异常，deliveryTag: {}", deliveryTag, e);
            // 手动拒绝消息
            try {
                channel.basicNack(deliveryTag, false, true);
            }catch (Exception ex){
                log.error("拒绝消息失败，deliveryTag: {}", deliveryTag, ex);
            }
        }

    }
}