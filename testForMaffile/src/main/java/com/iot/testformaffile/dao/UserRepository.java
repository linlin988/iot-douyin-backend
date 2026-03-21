package com.iot.testformaffile.dao;

import com.iot.testformaffile.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserRepository extends ElasticsearchRepository<User, Long> {

    // 按名字搜索
    List<User> findByName(String name);
}