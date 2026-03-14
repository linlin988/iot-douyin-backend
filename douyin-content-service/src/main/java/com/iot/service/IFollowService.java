package com.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.VO.FollowsVO;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Follows;

public interface IFollowService extends IService<Follows> {

    Result add(Long followedUserId, Boolean follow);

    Result queryFollow(Long followedUserId);

    Result getMyFollowList(pageQuery pageQuery);

    Result getMyFansList(pageQuery pageQuery);
}
