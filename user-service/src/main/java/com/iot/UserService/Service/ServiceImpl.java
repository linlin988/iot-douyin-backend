package com.iot.UserService.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.utils.Jwt.JwtUtils;
import com.iot.commonModules.utils.PasswordUtil.PasswordUtil;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Entity.User;
import com.iot.UserService.Mapper.UserMapper;
import com.iot.UserService.Service.UserService;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class ServiceImpl extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<UserMapper,User> implements UserService {
    private final PasswordUtil passwordUtil;
    private final JwtUtils jwtUtil;
    private final UserMapper userMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    //注册时检验用户是否存在
    public void register(UserRegisterDTO registerDTO){
        User existUser=UserMapper.selectByAccount(registerDTO.getAccount());
        if(existUser!=null){
            throw new RuntimeException("用户名/手机号已经存在");
        }
        String encryptedPwd=passwordUtil.encrypt(registerDTO.getPassword());//密码加密
        //创建用户
        User user=new User();
        user.setUsername(registerDTO.getAccount());//设置用户名
        user.getPassword(encryptedPwd);//设置加密密码
        user.setCreate_time(LocalDateTime.now());//设置创建时间
        user.setUpdate_time(LocalDateTime.now());//设置更新时间
        user.setPhone(registerDTO.getAccount());//设置手机号
        user.setFan_count(0);
        user.setFollow_count(0);
        UserMapper.insert(user);
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
        String token = jwtUtil.getToken();
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
}

}
