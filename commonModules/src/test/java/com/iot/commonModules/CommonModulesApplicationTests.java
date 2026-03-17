package com.iot.commonModules;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = CommonModulesApplicationTests.TestConfig.class)
class CommonModulesApplicationTests {

    @Configuration
    static class TestConfig {
        // 测试配置类
    }

    @Test
    void contextLoads() {
    }

}
