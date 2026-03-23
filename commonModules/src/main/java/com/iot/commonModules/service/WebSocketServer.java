package com.iot.commonModules.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {

    // 在线用户连接池
    public static final Map<Long, Session> SESSION_POOL = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        SESSION_POOL.put(userId, session);
        log.info("用户{} 连接WebSocket", userId);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSION_POOL.remove(userId);
        log.info("用户{} 断开WebSocket", userId);
    }

    @OnError
    public void onError(Throwable error) {
        log.error("WebSocket异常", error);
    }

   //给视频作者发送点赞通知
    public static void sendLikeNotify(Long videoAuthorId, String message) {
        Session session = SESSION_POOL.get(videoAuthorId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("推送点赞通知给视频作者{}: {}", videoAuthorId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}