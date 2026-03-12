package com.iot.gatewayservice.controller;

import com.iot.gatewayservice.entiy.User;
import com.iot.gatewayservice.mapper.UserMapper;
import com.iot.gatewayservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @PostMapping("/testForUser")
    public String saveUser(@RequestBody User user) {
        boolean result = userService.saveUser(user);
        if (result) {
            return "用户保存成功，ID：" + user.getId();
        } else {
            return "用户保存失败";
        }
    }
}
