package com.iot.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.commonModules.entity.Videos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


@Mapper
public interface VideoMapper extends BaseMapper<Videos> {

    @Update("UPDATE t_video SET play_count = play_count + 1 WHERE id = #{videoId}")
    void incrementPlayCount(Long videoId);
}


