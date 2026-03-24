package com.iot.commonModules.interceptors;

import cn.hutool.core.util.StrUtil;
import com.iot.commonModules.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class UserInfoInterceptors implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取登录用户信息
        String userId = request.getHeader("iot-User-Id");

        // 2. 判断：**不为空**才存入ThreadLocal
        if (StrUtil.isNotBlank(userId)) {
            UserContext.setUser(Long.valueOf(userId));
            log.info("拦截器已获取当前登录用户ID：{}", userId);
        }

        // 3. 放行
        return true;
    }


    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户信息，防止内存泄漏
        UserContext.removeUser();
    }
}