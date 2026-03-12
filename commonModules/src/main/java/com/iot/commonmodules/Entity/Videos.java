package com.iot.commonModules.Entity;

import lombok.Data;

@Data
public class Videos {
    private Integer id;
    private String userId;
    private String videoUrl;
    private String coverUrl;
    private String title;
    private String description;
    private Integer playCount;
    private Integer likeCount;
    private String createdAt;
}