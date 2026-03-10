package com.iot.commonmodules.Entity;


import lombok.Data;

@Data
public class Comments {
    private Integer id;
    private String userId;
    private String videoId;
    private String comments;
    private String createdAt;
}