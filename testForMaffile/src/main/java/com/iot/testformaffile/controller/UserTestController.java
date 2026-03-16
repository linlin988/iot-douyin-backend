package com.iot.testformaffile.controller;



import com.iot.commonModules.request.LoginRequest;
import com.iot.commonModules.service.CaptchaService;
import com.iot.commonModules.utils.Jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class UserTestController {
    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisTemplate redisTemplate;

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
    @GetMapping("/user/info")
    public String getUserInfo(@RequestHeader(value = "iot-User-Id", required = false) String userId,
                              @RequestHeader(value = "iot-User-Name", required = false) String username) {
        return "当前用户: " + username + " (ID: " + userId + ")";
    }

    @GetMapping("/user/token")
    public String getToken() {
        String userId = "1243";
        String username = "testuser";

        String token = JwtUtils.getToken(userId);

        String redisKey = "login:" + token;

        // 用 hash 存多个字段
        redisTemplate.opsForHash().put(redisKey, "userId", userId);
        redisTemplate.opsForHash().put(redisKey, "username", username);

        redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);

        return token;
    }
}
