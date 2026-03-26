package com.iot.UserService.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Dto.UserUpdateDTO;
import com.iot.UserService.Entity.User;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import com.iot.commonModules.DTO.PageDTO;

import java.util.List;

public interface UserService extends IService<User> {
    // 用户注册
    void register(UserRegisterDTO registerDTO);

    // 用户登录
    UserLoginVO login(UserLoginDTO loginDTO);

    // 根据用户ID查询用户信息
    UserInfoVO getUserInfoById(Long userId);

    // 根据用户名/手机号查询用户（内部使用）
    User getByAccount(String account);

    // 根据用户ID列表查询用户信息
    List<UserInfoVO> getUserInfoByIds(List<Long> ids);
    /**
     * 修改当前登录用户信息
     * @param userId 登录用户ID（从请求头iot-User-Id获取）
     * @param updateDTO 修改参数（昵称、头像、关注数增量、粉丝数增量）
     */
    void updateUserInfo(Long userId, UserUpdateDTO updateDTO);
    //查询用户头像地址
    String getUserAvatar(Long userId);

    // 根据用户ID查询用户名
    String getUsernameById(Long userId);
}
