package com.iot.testformaffile.config;

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
                .group("test-user")
                .packagesToScan("com.iot.testformaffile.controller")
                .pathsToMatch("/test/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iot接口文档")
                        .version("1.0")
                        .description("测试接口文档"));
    }
}