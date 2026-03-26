package com.iot.content.service;

import com.iot.commonModules.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iot.commonModules.entity.User;
import com.iot.commonModules.entity.Videos;
import com.iot.content.VO.VideoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    //视频上传
    Videos uploadVideo(MultipartFile file, MultipartFile coverfile, String title, String description, Long userId) throws Exception;

    //删除视频
    boolean deleteVideo(Long videoId, Long userId) throws Exception;

    //视频查询（分页）
    IPage<Videos> getVideoList(int page, int size);

    //视频详情与播放数统计
    Videos getVideoDetail(Long videoId);

    //查询用户发布的所有视频
    List<VideoVO> getUserWorksByUserId(Long userId);
  
    // 头像上传
    User uploadAvatar(MultipartFile file, Long userId) throws Exception;

    void recordPlayRedis(Long videoId, Long userId);
}
