package com.AIIR.Login.controller;


import com.AIIR.Login.service.RegistrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/login")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EmailVerificationController emailVerificationController;


    /**
     * 使用@RequestBody时，不能使用@GetMapping
     */
    @PostMapping("/register")
    @Transactional
    @GlobalTransactional
    public Map<String, Object> register(@RequestBody User user, @RequestParam String code) throws JsonProcessingException {
        try {
            log.info("用户名：{}，密码：{}，验证码：{}", user.getEmail(), user.getPassword(),code);
            Map<String, Object> stringObjectMap = emailVerificationController.verifyCode(user.getEmail(), code);
            if(!(boolean) stringObjectMap.get("success")){
                stringObjectMap.put("message", "验证码错误");
                stringObjectMap.put("success", false);
                return stringObjectMap;
            }
            registrationService.register(user);
            stringObjectMap.put("message", "注册成功");
            stringObjectMap.put("success", true);
            return stringObjectMap;
        } catch (JsonProcessingException e) {
            log.error("注册失败", e);
            throw new RuntimeException(e);
        }
    }
}
