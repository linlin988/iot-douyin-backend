package com.iot.UserService.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.UserService.Entity.Follow;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
public interface FollowMapper extends BaseMapper<Follow>{
    // 判断是否已关注：根据userId和followUserId查询
    @Select("select * from follow where user_id = #{userId} and follow_user_id = #{followUserId}")
    Follow selectByUserAndFollowUser(Long userId, Long followUserId);
}
