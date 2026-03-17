package com.iot.tiktok.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class VideoInterceptor implements HandlerInterceptor {

    // 网关必须传递的固定请求头（和网关里设置的一致）
    private static final String GATEWAY_HEADER = "X-Gateway-Request";
    private static final String GATEWAY_SECRET = "internal-gateway-123456";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 获取网关传递的请求头
        String gatewayHeader = request.getHeader(GATEWAY_HEADER);

        // 2. 校验：必须等于我们约定的密钥
        if (gatewayHeader == null || !gatewayHeader.equals(GATEWAY_SECRET)) {
            // 直接返回 401 禁止访问
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"禁止直接访问子服务，请通过网关访问\"}");
            return false;
        }

        // 3. 验证通过，放行
        return true;
    }
}