package com.iot.UserService.Controller;

import com.iot.commonModules.common.AllException;
import com.iot.commonModules.common.Result;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Dto.UserUpdateDTO;
import com.iot.UserService.Service.UserService;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import com.iot.commonModules.request.LoginRequest;
import com.iot.commonModules.service.CaptchaService;
import com.iot.commonModules.utils.Jwt.JwtUtils;
import com.iot.commonModules.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.iot.UserService.Vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "用户管理接口", description = "包含用户注册、登录、查询用户信息等接口") // 控制器注解
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private CaptchaService captchaService;

    //验证码
    @GetMapping(value = "/captcha",produces = "image/jpeg")
    public void getCaptcha(LoginRequest loginRequest, HttpServletResponse response) {
        captchaService.captcha(loginRequest, response);
    }

    // 用户注册接口
    @Operation(summary = "用户注册", description = "传入用户名/密码/手机号，完成用户注册，已存在则报错") // 接口注解
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        // 适配Result：自定义成功提示语，无数据返回
        return Result.success("注册成功！");
    }

    // 用户登录接口
    @Operation(summary = "用户登录", description = "传入账号/密码，登录成功返回token和用户基础信息")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserLoginVO loginVO = userService.login(loginDTO);
        // 适配Result：自定义提示语 + 返回登录数据（替换默认的"222"）
        return Result.success("登录成功！", loginVO);
    }

    // 查询当前登录用户信息接口
    @Operation(summary = "查询当前登录用户信息", description = "从请求头token解析userId，返回用户详细信息")
    @GetMapping("/info")
    public Result getCurrentUserInfo() {
       Long userId = UserContext.getUser();
        // 空值校验：userId不存在则抛自定义异常，由全局异常处理器返回标准化错误
        if (userId == null) {
            throw new AllException("未获取到登录用户信息", 401);
        }
        UserInfoVO infoVO = userService.getUserInfoById(userId);
        // 适配Result：自定义提示语 + 返回用户信息
        return Result.success("查询当前用户信息成功", infoVO);
    }

    // 根据用户ID查询用户信息
    @Operation(summary = "按ID查询用户信息", description = "传入用户ID，返回用户昵称、头像等基础信息，供评论/视频微服务调用")
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

    @Operation(summary = "按照ID集合查询用户信息",description = "传入集合ids，返回用户基本信息集合")
    @GetMapping("/ids")
    public List<UserInfoVO> getUserById(@RequestParam List<Long> ids) {
        List<UserInfoVO> infoVOs = userService.getUserInfoByIds(ids);
        return infoVOs;
    }
    //修改用户登录信息接口
    @Operation(
            summary = "修改当前登录用户信息",
            description = "从请求头iot-User-Id获取用户ID，支持修改昵称、头像、关注数增量、粉丝数增量",
            responses = {
                    @ApiResponse(responseCode = "200", description = "修改成功"),
                    @ApiResponse(responseCode = "401", description = "未获取到登录用户信息"),
                    @ApiResponse(responseCode = "500", description = "用户不存在或修改失败")
            }
    )
    @PutMapping("/info")
    public Result updateCurrentUserInfo(
            HttpServletRequest request,
            @Valid @RequestBody UserUpdateDTO updateDTO
    ) {
        Long userId = Long.valueOf(request.getHeader("iot-User-Id"));
        if (userId == null) {
            throw new AllException("未获取到登录用户信息", 401);
        }
        userService.updateUserInfo(userId, updateDTO);
        return Result.success("用户信息修改成功！");
    }
}