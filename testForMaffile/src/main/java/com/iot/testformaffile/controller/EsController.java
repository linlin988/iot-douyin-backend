package com.iot.testformaffile.controller;

import com.iot.testformaffile.dao.UserRepository;
import com.iot.testformaffile.entity.User;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class EsController {

    @Resource
    private UserRepository userRepository;

    // 1. 新增数据到 ES
    @GetMapping("es/add")
    public String add() {
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        user.setAge(20);
        user.setPhone("13800138000");
        userRepository.save(user);
        return "success";
    }

    // 2. 从 ES 查询
    @GetMapping("es/get")
    public List<User> get() {
        return userRepository.findByName("张三");
    }
}