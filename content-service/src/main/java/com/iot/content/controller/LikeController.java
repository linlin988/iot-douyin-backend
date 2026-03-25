package com.iot.content.controller;

import com.iot.commonModules.common.AllException;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Likes;
import com.iot.commonModules.utils.UserContext;
import com.iot.content.VO.VideoVO;
import com.iot.content.service.ILikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/like")
@Slf4j
@Tag(name = "点赞模块", description = "点赞相关接口")
public class LikeController {

    @Resource
    private ILikeService likeService;

    @Operation(summary = "点赞视频")
    @PutMapping("/{id}")
    public Result likeVideo(@PathVariable("id") Long videoId) {
        log.info("点赞视频：{}", videoId);
        return likeService.likeVideo(videoId);
    }

    @Operation(summary = "判断当前用户是否点赞该视频")
    @GetMapping("/isLike/{videoId}")
    public Result isLike(@PathVariable("videoId") Long videoId) {
        //判断当前登录用户是否点赞该视频，并返回结果
        return likeService.isLike(videoId);
    }


    @Operation(summary = "查询视频点赞数")
    @GetMapping("/likeCount/{id}")
    public Result likeCount(@PathVariable("id") Long videoId) {
        //查询视频点赞数
       return likeService.likeCount(videoId);
    }


    /**
     * @return 查看用户点赞列表（返回视频封面以及视频id）
     */
    @Operation(summary = "查看用户点赞列表（返回视频封面以及视频id）")
    @GetMapping("/myLike")
    public Result getUserLikeList() {
        Long userId = UserContext.getUser();
        List<VideoVO> likeList = likeService.getUserLikeVideoList(userId);
        if (likeList == null) {
            return Result.success("用户未点赞任何视频");
        }
        return Result.success("查询点赞列表成功", likeList);
    }
}
