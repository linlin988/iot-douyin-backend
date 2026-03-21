package com.iot.UserService.Feign;
import com.iot.commonModules.common.Result;
import com.iot.UserService.Vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// 调用的微服务名称：user-service
@FeignClient(value = "user-service")
public interface UserFeignClient {
    // 调用用户服务的根据ID查询用户信息接口
    @GetMapping("/user/info/{userId}")
    Result getUserInfoById(@PathVariable(value = "userId") Long userId);
}
