package com.iot.gatewayservice.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.gatewayservice.entiy.User;
import com.iot.gatewayservice.mapper.UserMapper;
import com.iot.gatewayservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean saveUser(User user) {
        return save(user);
    }
}