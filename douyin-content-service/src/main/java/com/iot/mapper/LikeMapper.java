package com.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.iot.commonModules.entity.Likes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper extends BaseMapper<Likes> {
}
