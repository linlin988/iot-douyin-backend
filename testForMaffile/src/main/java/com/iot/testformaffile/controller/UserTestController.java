package com.iot.testformaffile.controller;



import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.request.LoginRequest;
import com.iot.commonModules.service.CaptchaService;
import com.iot.commonModules.utils.Jwt.JwtUtils;
import com.iot.commonModules.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/maffile")
@Tag(name="网关自测用户模块",description = "在用户模块完成前测试网关功能")
public class UserTestController {
    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Operation(summary = "获取验证码并返回验证码图片")
    @PostMapping(value = "/captcha", produces = "image/jpeg")
    public void getCaptcha(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        captchaService.captcha(loginRequest,response);
    }

    @Operation(summary = "测试接口是否正常，此接口不在白名单")
    @GetMapping
    public String test(){
        return "test";
    }

    @Operation(summary = "主动抛出异常，测试统一异常处理")
    @GetMapping("/exception")
    public String throwException() {
        throw new RuntimeException("这是一个测试全局异常的 RuntimeException");  // 或自定义异常
        // return "ok";  // 正常不会走到这里
    }

    @Operation(summary = "查询登录信息")
    @GetMapping("/user/info")
    public String getUserInfo(@RequestHeader(value = "iot-User-Id", required = false) String userId,
                              @RequestHeader(value = "iot-User-Name", required = false) String username) {
        return "当前用户: " + username + " (ID: " + userId + ")";
    }

    @Operation(summary = "获取token1")
    @GetMapping("/user/token/1")
    public String getToken1() {
        String userId = "11113";
        String username = "testuser";

        String token = JwtUtils.getToken(userId);

        String redisKey = "login:" + token;

        // 用 hash 存多个字段
        redisTemplate.opsForHash().put(redisKey, "userId", userId);
        redisTemplate.opsForHash().put(redisKey, "username", username);

        redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);

        return token;
    }

    @Operation(summary = "获取token2")
    @GetMapping("/user/token/2")
    public String getToken2() {
        String userId = "66663";
        String username = "Newtestuser";

        String token = JwtUtils.getToken(userId);

        String redisKey = "login:" + token;

        // 用 hash 存多个字段
        redisTemplate.opsForHash().put(redisKey, "userId", userId);
        redisTemplate.opsForHash().put(redisKey, "username", username);

        redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);

        return token;
    }

    @Operation(summary = "写入登录信息")
    @GetMapping("/user/login")
    public Object login(@RequestHeader(value = "iot-User-Id", required = false) String userId){
        UserContext.setUser(Long.parseLong(userId));
        Long testID = UserContext.getUser();
        System.out.println("当前线程UserContext中的id：" + testID);
        return Result.success(testID);
    }
}
