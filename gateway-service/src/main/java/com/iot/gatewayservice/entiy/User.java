package com.iot.gatewayservice.entiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iot.commonmodules.Entity.Users;

@TableName("Users")
public class User extends Users {
    @TableId(type = IdType.AUTO)
    private Integer id;
}
