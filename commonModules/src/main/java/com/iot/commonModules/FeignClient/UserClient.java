package com.iot.commonModules.FeignClient;



import com.iot.commonModules.VO.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserClient {
    // 根据用户ID列表查询用户信息
    @GetMapping("/ids")
    List<UserInfoVO> getUserInfoByIds(@RequestParam("ids") List<Long> ids);
}