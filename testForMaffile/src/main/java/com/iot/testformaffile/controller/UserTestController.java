package com.iot.testformaffile.controller;



import com.iot.commonModules.request.LoginRequest;
import com.iot.commonModules.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class UserTestController {
    @Autowired
    private CaptchaService captchaService;

    @PostMapping(value = "/captcha", produces = "image/jpeg")
    public void getCaptcha(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        captchaService.captcha(loginRequest,response);
    }

    @GetMapping
    public String test(){
        return "test";
    }

    @GetMapping("/exception")
    public String throwException() {
        throw new RuntimeException("这是一个测试全局异常的 RuntimeException");  // 或自定义异常
        // return "ok";  // 正常不会走到这里
    }
}
