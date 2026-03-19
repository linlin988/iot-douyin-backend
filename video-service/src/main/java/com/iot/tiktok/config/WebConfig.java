package com.iot.tiktok.config;

import com.iot.tiktok.interceptor.VideoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    VideoInterceptor videoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(videoInterceptor)
                .addPathPatterns("/**")          // 拦截所有接口
                .excludePathPatterns(           // 放行白名单
                        "/login",
                        "/captcha",
                        "/actuator/**",
                        "/error",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**"
                );
    }
}