package com.iot.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication(scanBasePackages = {"com.iot.commonModules","com.iot.UserService"})// 扫描com.iot.commonModules和com.iot.UserService下所有包
@MapperScan("com.iot.UserService.Mapper") // 扫描Mapper接口
@EnableDiscoveryClient//开启nacos服务发现
@EnableFeignClients//微服务之间的调用，开启openfeign
public class userServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(userServiceApplication.class, args);
    }
}
