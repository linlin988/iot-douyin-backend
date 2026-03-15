package com.iot.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.commonModules.entity.Videos;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
public interface VideoMapper extends BaseMapper<Videos> {

}


