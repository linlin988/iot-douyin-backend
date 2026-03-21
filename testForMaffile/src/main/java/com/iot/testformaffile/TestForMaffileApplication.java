package com.iot.testformaffile;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication( scanBasePackages = "com.iot")
public class TestForMaffileApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestForMaffileApplication.class, args);
    }

}
