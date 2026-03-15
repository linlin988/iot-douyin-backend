package com.iot.gatewayservice.config;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import java.util.HashMap;
import java.util.Map;




@Component
public class GlobalFilterConfig implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求
        System.out.println("========== 过滤器执行了 ==========");
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 2. 放行登录、验证码接口
        if (path.contains("/login") || path.contains("/captcha")) {
            return chain.filter(exchange);
        }

        // 3. 获取 token
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            // 没 token → 直接返回 401
            return responseFail(exchange, "请先登录");
        }

        // 4. 校验 Redis 中是否存在（所有微服务共享 Redis，直接校验）
        Boolean hasKey = redisTemplate.hasKey("login:" + token);
        if (hasKey == null || !hasKey) {
            return responseFail(exchange, "登录已过期");
        }

        // 5. 放行前，标记是从网关进来的请求
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("iot-Gateway-Request", "internal-gateway-123456")
                .build();
        exchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(exchange);
    }

    // 返回错误信息
    private Mono<Void> responseFail(ServerWebExchange exchange, String msg) {
        // 这里你可以用你那个 ResultCodeEnum 枚举
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
