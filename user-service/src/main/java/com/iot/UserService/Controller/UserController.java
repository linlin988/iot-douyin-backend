package com.iot.UserService.Controller;

import com.iot.commonModules.common.AllException;
import com.iot.commonModules.common.Result;
import com.iot.UserService.Dto.UserLoginDTO;
import com.iot.UserService.Dto.UserRegisterDTO;
import com.iot.UserService.Dto.UserUpdateDTO; // 新增：修改用户信息的DTO
import com.iot.UserService.Service.UserService;
import com.iot.UserService.Vo.UserInfoVO;
import com.iot.UserService.Vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理接口", description = "包含用户注册、登录、查询用户信息、修改用户信息等接口") // 补充接口描述
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //注册接口
    @Operation(summary = "用户注册", description = "传入用户名/密码/手机号，完成用户注册，已存在则报错")
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功！");
    }

   //登录接口
    @Operation(summary = "用户登录", description = "传入账号/密码，登录成功返回token和用户基础信息")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserLoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功！", loginVO);
    }

    //查询用户登录信息接口
    @Operation(summary = "查询当前登录用户信息", description = "从请求头token解析userId，返回用户详细信息")
    @GetMapping("/info")
    public Result getCurrentUserInfo(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("iot-User-Id"));
        if (userId == null) {
            throw new AllException("未获取到登录用户信息", 401);
        }
        UserInfoVO infoVO = userService.getUserInfoById(userId);
        return Result.success("查询当前用户信息成功", infoVO);
    }

    //按id查询用户信息接口
    @Operation(summary = "按ID查询用户信息", description = "传入用户ID，返回用户昵称、头像等基础信息，供评论/视频微服务调用")
    @GetMapping("/info/{userId}")
    public Result getUserInfoById(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            throw new AllException("用户ID格式错误", 400);
        }
        UserInfoVO infoVO = userService.getUserInfoById(userId);
        return Result.success("查询用户信息成功", infoVO);
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