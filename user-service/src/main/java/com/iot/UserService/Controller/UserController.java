package com.iot.UserService.Controller;

import com.iot.commonModules.common.AllException;
import com.iot.commonModules.common.Result;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Service.UserService;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 用户注册
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        // 适配Result：自定义成功提示语，无数据返回
        return Result.success("注册成功！");
    }

    // 用户登录
    @PostMapping("/login")
    public Result login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserLoginVO loginVO = userService.login(loginDTO);
        // 适配Result：自定义提示语 + 返回登录数据（替换默认的"222"）
        return Result.success("登录成功！", loginVO);
    }

    // 查询当前登录用户信息（从请求域获取userId）
    @GetMapping("/info")
    public Result getCurrentUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 空值校验：userId不存在则抛自定义异常，由全局异常处理器返回标准化错误
        if (userId == null) {
            throw new AllException("未获取到登录用户信息", 401);
        }
        UserInfoVO infoVO = userService.getUserInfoById(userId);
        // 适配Result：自定义提示语 + 返回用户信息
        return Result.success("查询当前用户信息成功", infoVO);
    }

    // 根据用户ID查询用户信息（供其他服务调用，如comment服务）
    @GetMapping("/info/{userId}")
    public Result getUserInfoById(@PathVariable Long userId) {
        // 空值校验：路径参数userId为空则抛自定义异常
        if (userId == null || userId <= 0) {
            throw new AllException("用户ID格式错误", 400);
        }
        UserInfoVO infoVO = userService.getUserInfoById(userId);
        // 适配Result：自定义提示语 + 返回用户信息
        return Result.success("查询用户信息成功", infoVO);
    }

    @GetMapping("/ids")
    public List<UserInfoVO> getUserById(@RequestParam List<Long> ids) {
        List<UserInfoVO> infoVOs = userService.getUserInfoByIds(ids);
        return infoVOs;
    }
}