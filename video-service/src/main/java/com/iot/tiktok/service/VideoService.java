package com.iot.tiktok.service;


import com.iot.commonModules.entity.Videos;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface VideoService {

    //视频上传
    Videos uploadVideo(MultipartFile file, String title, String description) throws Exception;

    //视频查询（分页）
    List<Videos> getVideoList(int page, int size);

    //视频详情与播放数统计
    Videos getVideoDetail(Long videoId);
}
