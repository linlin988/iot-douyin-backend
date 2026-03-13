package com.iot.commonModules.utils.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean set(String key, String value) {
        try{
            redisTemplate.opsForValue().set(key, value);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public  Object get(String key) {
        return key==null?null:redisTemplate.opsForValue().get(key);
    }
    public boolean set(String key,Object value,long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value.toString());

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void del(String... key) {
        if (key != null && key.length > 0) {
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    public boolean hasKey(String  key) {
        try{
            return redisTemplate.hasKey(key);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


}



