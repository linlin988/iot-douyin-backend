package com.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.DTO.PageDTO;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.VO.FollowsVO;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Follows;
import com.iot.commonModules.entity.User;
import com.iot.mapper.FollowMapper;
import com.iot.mapper.UserMapper;
import com.iot.service.IFollowService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper,Follows> implements IFollowService  {



    @Resource
    UserMapper userMapper;



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
            Long followUserId = 1L;//TODO JWT TOKEN
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

    @Override
    public Result getMyFollowList(pageQuery pageQuery) {
        // 1.获取当前登录用户
        Long followUserId = 1L; // TODO: 实际应从 JWT token 中获取
        
        // 2.构建分页对象，使用默认按创建时间倒序排序
        Page<Follows> page = pageQuery.toMpPageDefaultSortByCreateTimeDesc();
        
        // 3.执行分页查询，查询我关注的用户（即我作为关注者的记录）
        Page<Follows> followsPage = query()
                .eq("follow_user_id", followUserId)
                .page(page);


        List<Long> userIds = followsPage.getRecords().stream()
        .map(Follows::getFollowedUserId)
        .collect(Collectors.toList());

        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
        .collect(Collectors.toMap(User::getId, u -> u));

        PageDTO<FollowsVO> voPage = PageDTO.of(followsPage, follows -> {
        User user = userMap.get(follows.getFollowedUserId());
        return user != null ? new FollowsVO(user.getAvatar(), user.getNickname()) : null;
        });

//        // 5.遍历关注列表，查询每个被关注用户的头像和昵称
//        for (Follows follows : followsPage.getRecords()) {
//            // 根据被关注者 ID 查询用户信息
//            User user = userMapper.selectById(follows.getFollowedUserId());
//            if (user != null) {
//                FollowsVO followsVO = new FollowsVO(
//                    user.getAvatar(),
//                    user.getNickname()
//                );
//                voPage.getRecords().add(followsVO);
//            }

        
        // 6.返回分页结果
        return Result.success(voPage);
    }

    @Override
    public Result getMyFansList(pageQuery pageQuery) {
        // 1.获取当前登录用户
        Long followUserId = 1L; // TODO: 实际应从 JWT token 中获取

        // 2.构建分页对象，使用默认按创建时间倒序排序
        Page<Follows> page = pageQuery.toMpPageDefaultSortByCreateTimeDesc();

        // 3.执行分页查询，查询我的粉丝（即关注我的用户）
        Page<Follows> followsPage = query()
                .eq("followed_user_id", followUserId)
                .page(page);


        List<Long> userIds = followsPage.getRecords().stream()
                .map(Follows::getFollowUserId)
                .collect(Collectors.toList());

        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        PageDTO<FollowsVO> voPage = PageDTO.of(followsPage, follows -> {
            User user = userMap.get(follows.getFollowUserId());
            return user != null ? new FollowsVO(user.getAvatar(), user.getNickname()) : null;
        });


        return Result.success(voPage);
    }
}



