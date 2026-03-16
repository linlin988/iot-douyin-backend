package com.iot.gatewayservice.config;


import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;


@Component
public class GlobalFilterConfig implements GlobalFilter, Ordered {

    @Autowired
    private GatewayAuthProperties authProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求
        System.out.println("========== 过滤器执行了 ==========");
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        System.out.println("请求路径: " + path);
        System.out.println("白名单列表: " + authProperties.getWhiteList());  // 或 authProperties.getWhiteList()

        // 2. 放行登录、验证码接口
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        // 3. 获取 token
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            // 没 token → 直接返回 401
            return responseFail(exchange, "请先登录");
        }

        // 4.校验 Redis
        String redisKey = "login:" + token;
        System.out.println("ru-token:" + redisKey);
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        if (hasKey == null || !hasKey) {
            return responseFail(exchange, "登录已过期");
        }


        // 5.从 Redis 获取用户信息
        String userId = (String) redisTemplate.opsForHash().get(redisKey, "userId");
        String username = (String) redisTemplate.opsForHash().get(redisKey, "username");
;

        if (userId == null) {
            return responseFail(exchange, "用户信息异常");
        }

        // 6. 放行前，标记是从网关进来的请求
        // 附加到请求头（子服务可直接读取）
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("iot-User-Id", userId)          // 用户 ID
                .header("iot-User-Name", username != null ? username : "")  // 用户名（如果有）
                .header("iot-Gateway-Request", "internal-gateway-123456")  // 保留原有
                .build();
        exchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(exchange);
    }

    private boolean isWhiteListed(String path) {
        if (authProperties.getWhiteList() == null || authProperties.getWhiteList().isEmpty()) {
            return false;
        }
        return authProperties.getWhiteList().stream().anyMatch(pattern -> pathMatcher.match(pattern.trim(), path));
    }

    // 返回错误信息
    private Mono<Void> responseFail(ServerWebExchange exchange, String msg) {
        // 这里可以用 枚举
        Map<String, Object> map = new HashMap<>();
        map.put("code", 401);
        map.put("message", msg);

        byte[] bytes = JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // 优先级最高
    }
}
