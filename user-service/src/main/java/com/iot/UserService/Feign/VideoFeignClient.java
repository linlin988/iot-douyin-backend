package com.iot.UserService.Feign;

import com.iot.commonModules.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 调用内容服务（视频 + 点赞）
@FeignClient(name = "content-service")
public interface VideoFeignClient {

    // 查询用户发布的作品 ID 列表
    @GetMapping("/video/user/works/{userId}")
    Result getUserWorks(@PathVariable("userId") Long userId);

    // 查询用户点赞的视频 ID 列表
    @GetMapping("/like/user/likeList/{userId}")
    Result getUserLikeList(@PathVariable("userId") Long userId);
}