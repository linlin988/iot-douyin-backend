package com.iot.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient  // 开启服务注册发现
@EnableScheduling
@EnableFeignClients(basePackages = {"com.iot.commonModules.FeignClient"})
@MapperScan("com.iot.content.mapper")
@SpringBootApplication(scanBasePackages = {"com.iot.commonModules", "com.iot.content"})
public class contentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(contentServiceApplication.class, args);
    }
}