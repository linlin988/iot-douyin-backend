package com.iot.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iot.commonModules.DTO.PageDTO;
import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.FeignClient.UserClient;
import com.iot.commonModules.VO.UserInfoVO;
import com.iot.commonModules.common.Result;
import com.iot.commonModules.entity.Follows;
import com.iot.commonModules.entity.User;
import com.iot.commonModules.utils.UserContext;
import com.iot.content.VO.FollowsVO;
import com.iot.content.service.IFollowService;
import com.iot.content.mapper.FollowMapper;
import com.iot.content.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper,Follows> implements IFollowService {

    @Resource
    UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserClient  userClient;



    /**
     * 添加关注或取消关注
     * @param followedUserId 被关注用户ID
     * @param follow 是否关注
     * @return
     */
    public Result add(Long followedUserId, Boolean follow) {

        //获取当前用户id
        Long followUserId = UserContext.getUser();
        String key = "follow:" + followUserId;
        //1.根据follow确定是否关注
        if (follow) {
            //2.取关，删除数据
            boolean remove = remove(new QueryWrapper<Follows>().eq("followed_user_id", followedUserId)
                    .eq("follow_user_id", followUserId));
            if ( remove) {
                userMapper.updateFollowCount(followUserId, -1); // 取关，减少关注者关注数
                userMapper.updateFansCount(followedUserId, -1);
                stringRedisTemplate.opsForSet().remove(key, followedUserId.toString());
            }// 取关，减少被关注者粉丝数
        } else {
            Follows follows = new Follows();
            follows.setFollowedUserId(followedUserId);
            follows.setFollowUserId(followUserId);
            //3.关注，新增数据
            boolean save = save(follows);
            if (save) {
                //sadd 把被关注者ID加入redis的set集合
                stringRedisTemplate.opsForSet().add(key, followedUserId.toString());
                userMapper.updateFollowCount(followUserId, 1); // 关注，增加关注者关注数
                userMapper.updateFansCount(followedUserId, 1); // 关注，增加被关注者粉丝数
            }
        }
        return Result.success();
    }

    @Override
    public Result queryFollow(Long followedUserId) {
            // 1.获取登录用户
            Long followUserId = UserContext.getUser();
            // 2.查询是否关注 select count(*) from tb_follow where user_id = ? and follow_user_id = ?
            Integer count = Math.toIntExact(query().eq("follow_user_id", followUserId)
                    .eq("followed_user_id", followedUserId).count());
            // 3.判断
            if (count > 0){
                return Result.success("true");
            }else {
                return Result.success("false");
            }
        }

    @Override
    public Result getMyFollowList(pageQuery pageQuery) {
        Long followUserId = UserContext.getUser();
        Page<Follows> page = pageQuery.toMpPageDefaultSortByCreateTimeDesc();
        Page<Follows> followsPage = query()
                .eq("follow_user_id", followUserId)
                .page(page);

        if (followsPage.getRecords().isEmpty()) {
            return Result.success(PageDTO.empty(followsPage));
        }

        List<Long> userIds = followsPage.getRecords().stream()
                .map(Follows::getFollowedUserId)
                .collect(Collectors.toList());

        List<UserInfoVO> userInfoList = userClient.getUserInfoByIds(userIds);

        Map<Long, UserInfoVO> userMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfoVO::getId, user -> user, (v1, v2) -> v1));

        PageDTO<FollowsVO> voPage = PageDTO.of(followsPage, follow -> {
            UserInfoVO user = userMap.get(follow.getFollowedUserId());
            if (user == null) {
                return new FollowsVO("", "未知用户");
            }
            return new FollowsVO(user.getAvatar(), user.getUsername());
        });

        return Result.success(voPage);

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
    }

    @Override
    public Result getMyFansList(pageQuery pageQuery) {
        // 1.获取当前登录用户
        Long followUserId = UserContext.getUser();

        // 2.构建分页对象，使用默认按创建时间倒序排序
        Page<Follows> page = pageQuery.toMpPageDefaultSortByCreateTimeDesc();

        // 3.执行分页查询，查询我的粉丝（即关注我的用户）
        Page<Follows> followsPage = query()
                .eq("followed_user_id", followUserId)
                .page(page);

        // 4.从关注记录中提取粉丝ID
        List<Long> userIds = followsPage.getRecords().stream()
                .map(Follows::getFollowUserId)
                .collect(Collectors.toList());

        // 如果没有粉丝记录，直接返回空结果
        if (userIds.isEmpty()) {
            PageDTO<FollowsVO> emptyPage = PageDTO.of(followsPage, follows -> null);
            return Result.success(emptyPage);
        }

        List<UserInfoVO> userInfoList = userClient.getUserInfoByIds(userIds);

        // 5.将粉丝信息转换为VO
        Map<Long, UserInfoVO> userMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfoVO::getId, user -> user, (v1, v2) -> v1));

        PageDTO<FollowsVO> voPage = PageDTO.of(followsPage, follow -> {
            UserInfoVO user = userMap.get(follow.getFollowUserId());
            if (user == null) {
                return new FollowsVO("", "未知用户");
            }
            return new FollowsVO(user.getAvatar(), user.getUsername());
        });

        return Result.success(voPage);
    }

    /**
     * 获取关注数量
     * @return
     */
    @Override
    public Result getFollowCountByUserId() {
        Long userId = UserContext.getUser();
        Integer count = userMapper.queryFollowCount(userId);
        return Result.success(count);
    }

    /**
     * 获取粉丝数量
     * @return
     */
    @Override
    public Result getFansCountByUserId() {
        Long userId = UserContext.getUser();
        Integer count = userMapper.queryFansCount(userId);
        return Result.success(count);
    }

    @Override
    public Result getCommonFollowList(Long itsId) {
        Long  userId = UserContext.getUser();
        String key = "follow:" + userId ;
        String key2 = "follow:" + itsId;

        Set<String> commonFollows = stringRedisTemplate.opsForSet().intersect(key, key2);

        if (CollUtil.isNotEmpty(commonFollows)){
        List<Long> commonFollowIds = commonFollows.stream().map(Long::valueOf).collect(Collectors.toList());
        List<UserInfoVO> userInfoList = userClient.getUserInfoByIds(commonFollowIds);

        List<FollowsVO> voList = userInfoList.stream()
                .map(user -> new FollowsVO(user.getAvatar(), user.getUsername()))
                .collect(Collectors.toList());
        return Result.success(voList);
        }
        else return Result.success(Collections.emptyList());
    }
}