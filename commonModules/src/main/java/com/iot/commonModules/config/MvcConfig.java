package com.iot.commonModules.config;

import com.iot.commonModules.interceptors.UserInfoInterceptors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserInfoInterceptors userInfoInterceptors;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptors);
        log.info("添加用户信息拦截器成功");
    }
}
