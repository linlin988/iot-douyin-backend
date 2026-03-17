package com.iot.content.controller;

import com.iot.commonModules.common.Result;
import com.iot.content.service.ILikeService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@Slf4j
@Schema(name = "点赞模块", description = "点赞相关接口")
public class LikeController {

    @Resource
    private ILikeService likeService;

    @PutMapping("{id}")
    public Result likeVideo(@PathVariable("id") Long videoId) {
        return likeService.likeVideo(videoId);
    }

    @GetMapping("/isLike/{videoId}/{userId}")
    public Result isLike(@PathVariable("videoId") Long videoId, @PathVariable("userId") Long userId) {
        //判断当前登录用户是否点赞该视频，并返回结果
        return likeService.isLike(videoId, userId);
    }

}
