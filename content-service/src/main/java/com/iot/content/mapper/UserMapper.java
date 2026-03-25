package com.iot.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.commonModules.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 更新用户关注数
     * @param followUserId
     * @param delta 变化量 (正数增加，负数减少)
     */
    @Update("update t_user set follow_count = follow_count + #{delta} where id = #{followUserId}")
    void updateFollowCount(@Param("followUserId") Long followUserId, @Param("delta") int delta);

    /**
     * 更新用户粉丝数
     * @param followedUserId
     * @param delta 变化量 (正数增加，负数减少)
     */
    @Update("update t_user set fan_count = fan_count + #{delta} where id = #{followedUserId}")
    void updateFansCount(@Param("followedUserId") Long followedUserId, @Param("delta") int delta);

    /**
     * 查询用户关注数
     * @param userId
     * @return
     */
    @Select("select follow_count from t_user where id = #{userId}")
    Integer queryFollowCount(Long userId);

    /**
     * 查询用户粉丝数
     * @param userId
     * @return
     */
    @Select("select fan_count from t_user where id = #{userId}")
    Integer queryFansCount(Long userId);

    @Update({
            "<script>",
            "update t_user ",
            "<set>",
            "   <if test='avatar != null'>avatar = #{avatar},</if>",
            "   <if test='username != null'>username = #{username},</if>",
            "   <if test='password != null'>password = #{password},</if>",
            "</set>",
            "where id = #{id}",
            "</script>"
    })
    int updateById(User user);

    /**
     * 根据ID查询用户
     */
    @Select("select * from t_user where id = #{userId}")
    User selectById(@Param("userId") Long userId);
}