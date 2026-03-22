package com.AIIR.Login.controller;

import com.AIIR.Login.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService verificationService;

    // 发送验证码接口
    @PostMapping("/login/register/sendEmailCode")
    public Map<String, Object> sendVerificationCodeByRegister(@RequestParam("email") String email) {
        Map<String, Object> result = new HashMap<>();

        try {
            if(verificationService.checkEmailByRegister(email)){
                result.put("success", false);
                result.put("message", "该邮箱已注册");
                return result;
            }
            // 发送验证码
            boolean isSent = verificationService.sendVerificationCode(email);

            if (isSent) {
                result.put("success", true);
                result.put("message", "验证码已发送至您的邮箱");
            } else {
                result.put("success", false);
                result.put("message", "验证码发送失败，请稍后再试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务器内部错误");
            e.printStackTrace();
        }

        return result;
    }

    @PostMapping("/login/forgetPassword/sendEmailCode")
    public Map<String, Object> sendVerificationCodeByFindPassword(@RequestParam("email") String email) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 发送验证码
            boolean isSent = verificationService.sendVerificationCode(email);

            if (isSent) {
                result.put("success", true);
                result.put("message", "验证码已发送至您的邮箱");
            } else {
                result.put("success", false);
                result.put("message", "验证码发送失败，请稍后再试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务器内部错误");
            e.printStackTrace();
        }

        return result;
    }

    // 验证验证码接口
    @PostMapping("/verifyEmailCode")
    public Map<String, Object> verifyCode(
            @RequestParam String email,
            @RequestParam String verificationCode) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证验证码
            boolean isValid = verificationService.validateCode(email, verificationCode);

            if (isValid) {
                result.put("success", true);
                result.put("message", "验证码验证成功");
            } else {
                result.put("success", false);
                result.put("message", "验证码不正确或已过期");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务器内部错误");
            e.printStackTrace();
        }

        return result;
    }
}