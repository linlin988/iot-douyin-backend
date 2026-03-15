package com.iot.testformaffile.config;

import com.iot.testformaffile.interceptor.GatewayAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GatewayAuthInterceptor())
                .addPathPatterns("/**")          // 拦截所有接口
                .excludePathPatterns(           // 放行白名单
                        "/login",
                        "/captcha",
                        "/actuator/**",
                        "/error"
                );
    }
}