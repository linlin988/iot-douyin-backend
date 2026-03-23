package com.iot.UserService.Service;

import com.iot.commonModules.utils.Jwt.JwtUtils;
import com.iot.commonModules.utils.PasswordUtil.PasswordUtil;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Dto.UserUpdateDTO; // 新增导入：修改用户信息的DTO
import com.iot.UserService.Entity.User;
import com.iot.UserService.Mapper.UserMapper;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // 新增导入：空值判断

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordUtil passwordUtil;
    private final JwtUtils jwtUtil;
    private final UserMapper userMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO registerDTO) {
        User existUser = UserMapper.selectByAccount(registerDTO.getAccount());
        if (existUser != null) {
            throw new RuntimeException("用户名/手机号已经存在");
        }
        String encryptedPwd = passwordUtil.encrypt(registerDTO.getPassword());//密码加密
        //创建用户
        User user = new User();
        user.setUsername(registerDTO.getAccount());//设置用户名
        user.setPassword(encryptedPwd);//设置加密密码
        user.setCreate_time(LocalDateTime.now());//设置创建时间
        user.setUpdate_time(LocalDateTime.now());//设置更新时间
        user.setPhone(registerDTO.getAccount());//设置手机号
        user.setFan_count(0);
        user.setFollow_count(0);
        userMapper.insert(user);
    }
    @Override
    public UserLoginVO login(UserLoginDTO loginDTO) {
        // 1. 校验用户是否存在
        User user = UserMapper.selectByAccount(loginDTO.getAccount());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 2. 校验密码
        if (!passwordUtil.match(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 3. 生成JWT Token
        String token = jwtUtil.getToken(user.getId());
        // 4. 构建返回VO
        UserLoginVO loginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, loginVO);
        loginVO.setToken(token);
        return loginVO;
    }

    @Override
    public UserInfoVO getUserInfoById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserInfoVO infoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, infoVO);
        return infoVO;
    }

    @Override
    public User getByAccount(String account) {
        return UserMapper.selectByAccount(account);
    }

    //修改用户信息
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务保证原子性
    public void updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        // 1. 基础校验：用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 更新头像+昵称（
        // 空值处理：不传则保留原有值，避免更新为null
        String finalAvatar = StringUtils.hasText(updateDTO.getAvatar()) ? updateDTO.getAvatar() : user.getAvatar();
        String finalNickname = StringUtils.hasText(updateDTO.getNickname()) ? updateDTO.getNickname() : user.getUsername(); // 你实体类用的是username，数据库是nickname？需确认字段映射
        if (!finalAvatar.equals(user.getAvatar()) || !finalNickname.equals(user.getUsername())) {
            userMapper.updateUserInfo(userId, finalAvatar, finalNickname);
            // 同步更新update_time（你原有代码有update_time字段）
            user.setUpdate_time(LocalDateTime.now());
            userMapper.updateById(user); // 仅更新时间，不影响其他字段
        }

        // 3. 更新关注数
        if (updateDTO.getFollowCountDelta() != null) {
            userMapper.updateFollowCount(updateDTO.getFollowCountDelta(), userId);
        }

        // 4. 更新粉丝数
        if (updateDTO.getFanCountDelta() != null) {
            userMapper.updateFanCount(userId, updateDTO.getFanCountDelta());
        }
    }
}