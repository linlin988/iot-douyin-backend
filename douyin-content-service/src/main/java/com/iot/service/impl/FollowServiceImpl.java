package com.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Follows;
import com.iot.mapper.FollowMapper;
import com.iot.service.IFollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper,Follows> implements IFollowService  {


    @Autowired
    FollowMapper followMapper;



    public Result add(Long followedUserId, Boolean follow) {

        //获取当前用户id
        Long followUserId = 1L;

        //1.根据follow确定是否关注
        if (follow) {
            //2.取关，删除数据
            remove(new QueryWrapper<Follows>().eq("followed_user_id", followedUserId)
                    .eq("follow_user_id", followUserId));
        } else {
            Follows follows = new Follows();
            follows.setFollowedUserId(followedUserId);
            follows.setFollowedUserId(followUserId);
            //3.关注，新增数据
            save(follows);
        }
        return Result.success();
    }

    @Override
    public Result queryFollow(Long followedUserId) {
            // 1.获取登录用户
            Long followUserId = 1L;
            // 2.查询是否关注 select count(*) from tb_follow where user_id = ? and follow_user_id = ?
            Integer count = Math.toIntExact(query().eq("follow_user_id", followUserId)
                    .eq("followed_user_id", followedUserId).count());
            // 3.判断
            if (count > 0){
                return Result.success();
            }else {
                return Result.error();
            }
        }
    }



