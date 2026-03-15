package com.iot.tiktok.controller;

import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Videos;
import com.iot.tiktok.mapper.VideoMapper;
import com.iot.tiktok.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
public class VideoController {

    @Autowired
    VideoService videoService;

    //视频上传
    @PostMapping("/upload")
    public Result uploadVideo(
        @RequestParam("file") MultipartFile file,
        @RequestParam("title") String title,
        @RequestParam("description") String description) throws Exception {

        Videos video = videoService.uploadVideo(file, title, description);
        return Result.success(video);
    }

    //视频列表查询
    @GetMapping("/list")
    public Result getVideoList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Videos> videoList = videoService.getVideoList(page, size);
        return Result.success(videoList);
    }
    //视频详情查询
    @GetMapping("/detail/{videoId}")
    public Result getVideoDetail(@PathVariable Long videoId) {
        Videos video = videoService.getVideoDetail(videoId);
        return Result.success(video);
    }
}
