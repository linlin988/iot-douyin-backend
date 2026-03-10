package com.iot.commonmodules.Entity;

import lombok.Data;

@Data
public class Follows {
    private Integer id;
    private String userId;
    private String followerId;
    private String followeeId;
    private String createdAt;
}