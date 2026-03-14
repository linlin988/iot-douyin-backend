package com.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.iot")
public class contentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(contentServiceApplication.class, args);
    }
}
