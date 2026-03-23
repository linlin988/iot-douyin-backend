package com.iot.content.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iot.commonModules.entity.Videos;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    //视频上传
    Videos uploadVideo(MultipartFile file, MultipartFile coverfile, String title, String description, Long userId) throws Exception;

    //删除视频
    boolean deleteVideo(Long videoId, Long userId) throws Exception;

    //视频查询（分页）
    IPage<Videos> getVideoList(int page, int size);

    //视频详情与播放数统计
    Videos getVideoDetail(Long videoId);
}
