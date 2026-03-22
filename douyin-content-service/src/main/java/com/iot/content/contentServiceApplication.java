package com.iot.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableDiscoveryClient  // 开启服务注册发现
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.iot.commonModules", "com.iot.content"})// 只扫公共模块（工具、配置）
public class contentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(contentServiceApplication.class, args);
    }
}
