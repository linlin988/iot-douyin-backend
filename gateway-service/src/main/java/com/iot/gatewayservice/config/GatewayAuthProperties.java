package com.iot.gatewayservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "gateway.auth")  // 前缀对应 yml 的 gateway.auth
@Data
public class GatewayAuthProperties {

    private List<String> whiteList = new ArrayList<>();

    // 可以加其他属性，以后扩展方便
}