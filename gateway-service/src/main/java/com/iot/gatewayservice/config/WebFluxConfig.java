package com.iot.gatewayservice.config;

import org.springframework.boot.autoconfigure.web.WebProperties;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;

@Configuration
public class WebFluxConfig {

    // 手动注册ErrorAttributes，解决找不到Bean的问题
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }

    // 手动注册WebProperties
    @Bean
    public WebProperties webProperties() {
        return new WebProperties();
    }

    // 手动注册ServerCodecConfigurer
    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }
}