package com.iot.commonModules.interceptors;

import cn.hutool.core.util.StrUtil;
import com.iot.commonModules.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInfoInterceptors implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取登录用户信息
        String userId = request.getHeader("userId");
        //2.判断是否获取用户，如果有，存入ThreadLocal
        if (StrUtil.isNotBlank(userId)) {
            UserContext.setUser(Long.valueOf(userId));
        }
        //3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //1.移除ThreadLocal中的用户信息
        UserContext.removeUser();
    }
}
