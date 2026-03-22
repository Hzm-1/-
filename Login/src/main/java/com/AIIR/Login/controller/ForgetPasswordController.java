package com.AIIR.Login.controller;



import com.AIIR.Login.service.EmailVerificationService;
import com.AIIR.Login.service.ForgetPasswordService;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login")
public class ForgetPasswordController {

    @Autowired
    private ForgetPasswordService forgetPasswordService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/forgetPassword")
    public Map<String, Object> forgetPassword(@RequestBody User user, @RequestParam String code) {
        Map<String, Object> result = new HashMap<>();
        log.info("用户名：{}，密码：{}", user.getUsername(), user.getPassword());
        if (!emailVerificationService.validateCode(user.getEmail(), code)) {
            result.put("success", false);
            result.put("message", "验证码不正确或已过期");
            return result;
        }
        forgetPasswordService.forgetPassword(user);
        result.put("success", true);
        result.put("message", "密码修改成功");
        return result;
    }
}
