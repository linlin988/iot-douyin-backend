package com.iot.UserService.Service;
import cn.hutool.core.util.StrUtil;
import com.iot.commonModules.DTO.PageDTO;
import com.iot.commonModules.utils.Jwt.JwtUtils;
import com.iot.commonModules.utils.PasswordUtil.PasswordUtil;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Dto.UserUpdateDTO;
import com.iot.UserService.Entity.User;
import com.iot.UserService.Mapper.UserMapper;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.management.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<UserMapper,User> implements UserService {
    private final PasswordUtil passwordUtil;
    private final JwtUtils jwtUtil;
    @Autowired
    UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    //注册时检验用户是否存在
    public void register(UserRegisterDTO registerDTO){
        User existUser=userMapper.selectByAccount(registerDTO.getAccount());
        if(existUser!=null){
            throw new RuntimeException("用户名/手机号已经存在");
        }
        String encryptedPwd=passwordUtil.encrypt(registerDTO.getPassword());//密码加密
        //创建用户
        User user=new User();
        user.setUsername(registerDTO.getAccount());//设置用户名
        user.setPassword(encryptedPwd);//设置加密密码
        user.setPhone(registerDTO.getAccount());//设置手机号
        user.setFan_count(0);
        user.setFollow_count(0);
        userMapper.insert(user);
    }
    @Override
    public UserLoginVO login(UserLoginDTO loginDTO) {
        // 前端传的：Redis 的 key（来自获取验证码接口的响应头 Captcha-Key）
        String captchaKey = loginDTO.getSetKey();
        // 用户输入的验证码内容
        String userInputCode = loginDTO.getCaptcha();
        if (StrUtil.isBlank(captchaKey) || StrUtil.isBlank(userInputCode)) {
            throw new RuntimeException("验证码不能为空");
        }
        //从 Redis 获取正确验证码
        String correctCode = stringRedisTemplate.opsForValue().get(captchaKey);
        if (correctCode == null) {
            throw new RuntimeException("验证码已过期或不存在");
        }

        if (!userInputCode.equalsIgnoreCase(correctCode)) {
            throw new RuntimeException("验证码不正确");
        }

        stringRedisTemplate.delete(captchaKey);


        // 1. 校验用户是否存在
        User user = userMapper.selectByAccount(loginDTO.getAccount());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 2. 校验密码
        if (!passwordUtil.match(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String username = user.getUsername();
        Long userId = user.getId();
        // 3. 生成JWT Token
        String token = JwtUtils.getToken(userId);
        String redisKey = "login:" + token;
        redisTemplate.opsForHash().put(redisKey, "userId", String.valueOf(userId));
        redisTemplate.opsForHash().put(redisKey, "username", username);

        redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);
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
        return userMapper.selectByAccount(account);
    }

    @Override
    public List<UserInfoVO> getUserInfoByIds(List<Long> ids) {
        List<User> users = userMapper.selectBatchIds(ids);
        return users.stream()
                .map(user -> {
                    UserInfoVO infoVO = new UserInfoVO();
                    BeanUtils.copyProperties(user, infoVO);
                    return infoVO;
                })
                .collect(Collectors.toList());
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
            user.setUpdateTime(LocalDateTime.now());
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


