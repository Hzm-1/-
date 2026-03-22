package com.AIIR.Login.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import model.login.User;
import model.login.UserVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisOperate {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的默认行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 获取redis存储的缓存的登录信息
     */
    public User getRedisLogin(String key){
        User list = null;
        String json = redisTemplate.opsForValue().get(key);

        try {
            // 将JSON解析为User对象
            list = objectMapper.readValue(json, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * redis缓存登录信息
     */
    public void setRedisLogin(UserVO user){
        try {
            // 将对象转换为JSON字符串
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(String.valueOf(user.getId()),json,30, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
