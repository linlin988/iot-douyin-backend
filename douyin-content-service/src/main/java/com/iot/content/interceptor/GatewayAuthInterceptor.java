package com.iot.content.interceptor;

import cn.hutool.json.JSONUtil;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import static cn.hutool.core.lang.Console.log;

@Slf4j
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

        String userIdStr = request.getHeader("iot-User-Id"); // 从请求头拿用户ID

        //存入 UserContext
        if (userIdStr != null && !userIdStr.isEmpty()) {
            Long userId = Long.parseLong(userIdStr);
            UserContext.setUser(userId);
        }else {
            log.info("无id");
        }
        log.info(UserContext.getUser().toString());
        return true;
    }
}