package com.iot.commonModules.Entity;

import lombok.Data;

@Data
public class Likes {
    private Integer id;
    private String userId;
    private String videoId;
    private String createdAt;
}