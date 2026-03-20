package com.iot.testformaffile.controller;

import com.iot.testformaffile.service.ProducerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitController {

    @Resource
    private ProducerService producerService;

    @GetMapping("send")
    public String send() {
        producerService.sendMessage("这是发来的测试消息");
        return "发送成功";
    }
}