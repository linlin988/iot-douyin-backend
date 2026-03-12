package com.iot.gatewayservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.gatewayservice.entiy.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends IService<User> {
    public boolean saveUser(User user);
}
