package com.iot.content.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi contentApi() {
        return GroupedOpenApi.builder()
                .group("content-service")
                .packagesToScan("com.iot.content.controller")
                .pathsToMatch("/follow/**", "/comment/**", "/like/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iot-关注模块接口文档")
                        .version("1.0")
                        .description("关注模块测试接口文档"));
    }
}