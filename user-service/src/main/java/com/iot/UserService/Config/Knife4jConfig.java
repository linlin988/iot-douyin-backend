package com.iot.UserService.Config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI().info(new Info().title("用户微服务接口文档").version("1.0.0").description("用户微服务包含用户注册登录查询用户信息等接口"));
    }
}
