package com.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.commonModules.entity.Follows;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowMapper extends BaseMapper<Follows> {
}
