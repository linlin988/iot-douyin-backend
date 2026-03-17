package com.iot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder()
                .group("follow-service")
                .packagesToScan("com.iot.controller")
                .pathsToMatch("/follow/**")
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