package com.iot.testformaffile.interceptor;

import cn.hutool.json.JSONUtil;
import com.iot.commonModules.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class GatewayAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String gatewayHeader = request.getHeader("iot-Gateway-Request");
        if (gatewayHeader == null || !gatewayHeader.equals("internal-gateway-123456")) {
            response.setContentType("application/json;charset=utf-8");
            Result result = Result.error(401, "禁止直接访问子服务，请通过网关访问");
            response.getWriter().write(JSONUtil.toJsonStr(result));
            return false;
        }
        return true;
    }
}