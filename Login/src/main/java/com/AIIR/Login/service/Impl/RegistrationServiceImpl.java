package com.AIIR.Login.service.Impl;

import com.AIIR.Login.feign.RabbitMQFeignClient;
import com.AIIR.Login.mapper.LoginMapper;


import com.AIIR.Login.service.RegistrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private RabbitMQFeignClient rabbitMQFeignClient;

    /**
     * 将注册的用户信息同步到 ES中，采用RabbitMQ发送消息后由ES接收消息后插入数据
     */
    @Override
    @GlobalTransactional
    @Transactional
    public void register(User user) throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        user.setNickname(user.getEmail());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setUsername("用户"+ UUID.randomUUID());
        user.setStatus(1);
        loginMapper.registerByEmail(user);

        // 创建ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的默认行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        log.info("发送数据：{}", objectMapper.writeValueAsString(user));
        rabbitMQFeignClient.sendEsTask("ChatDemo.Elasticsearch", "es_sync_routing_key", objectMapper.writeValueAsString(user));
    }
}
