package com.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Follows;

public interface IFollowService extends IService<Follows> {

    Result add(Long followedUserId, Boolean follow);

    Result queryFollow(Long followedUserId);
}
