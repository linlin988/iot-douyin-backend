package com.iot.tiktok.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Videos;
import com.iot.tiktok.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/video")
@Tag(name = "视频模块",description = "视频上传，查询，统计")
public class VideoController {

    @Autowired
    VideoService videoService;

    //视频上传
    @Operation(summary = "视频上传")
    @PostMapping("/upload")
    public Result uploadVideo(
        @RequestParam("file") MultipartFile file,
        @RequestParam("coverfile" ) MultipartFile coverfile,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestHeader("userId") Long userId) throws Exception {

        Videos video = videoService.uploadVideo(file, coverfile, title, description,  userId);
        if(video != null)
            return Result.success(video);
        return Result.error(500,"视频上传失败");
    }


    //视频列表查询
    @Operation(summary = "视频列表查询")
    @GetMapping("/list")
    public Result getVideoList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Videos> videoPage= videoService.getVideoList(page, size);
        return Result.success(videoPage);
    }


    //视频详情查询与统计
    @Operation(summary = "视频详情查询与统计")
    @GetMapping("/detail/{videoId}")
    public Result getVideoDetail(@PathVariable Long videoId) {
        Videos video = videoService.getVideoDetail(videoId);
        return Result.success(video);
    }
}
