package com.AIIR.Login.service;


import java.math.BigInteger;

public interface EmailVerificationService {
    /**
     * 发送验证码
     */
    boolean sendVerificationCode(String email);

    /**
     * 验证验证码
     */
    boolean validateCode(String email, String inputCode);

    /**
     * 在注册功能中根据email检查该邮箱是否已注册
     */
    boolean checkEmailByRegister(String email);
}
