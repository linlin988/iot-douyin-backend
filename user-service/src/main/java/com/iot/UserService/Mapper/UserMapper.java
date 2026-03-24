package com.iot.UserService.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.UserService.Entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
@Repository
public interface UserMapper extends BaseMapper<User>{
    @Select("select * from t_user where username=#{account} or phone=#{account}")
    //通过用户名或者手机号查询用户
    User selectByAccount(String account);


    @Update("update t_user set avatar=#{avatar},nickname=#{nickname} where id=#{id}")//更新用户头像和昵称
    int updateUserInfo(@Param("id") long id,@Param("avatar") String avatar,@Param("nickname") String nickname);
    @Update("update t_user set follow_count=follow_count+#{count} where id=#{id}")//更新用户关注数
    int updateFollowCount(@Param("count") Integer count,@Param("id") long id);
    @Update("update t_user set fan_count=fan_count+#{count} where id=#{id}")//更新用户粉丝数
    int updateFanCount(@Param("id") long id,@Param("count") Integer count);

}
