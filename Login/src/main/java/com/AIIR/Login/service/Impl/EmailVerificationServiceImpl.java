package com.AIIR.Login.service.Impl;



import com.AIIR.Login.mapper.LoginMapper;
import com.AIIR.Login.service.EmailVerificationService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    // 验证码有效期（5分钟）
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;

    // 邮箱配置
    private static final String host = "smtp.qq.com"; // SMTP服务器地址
    private final String username = "188174648@qq.com"; // 发件人邮箱
    private final String password = "bzmyhmesenuhbhgg"; // 发件人邮箱密码或授权码
    private static final int port = 587; // SMTP端口，通常为587或465
    private static final String VerificationCode = "VerificationCode:";
    @Autowired
    private LoggingRebinder loggingRebinder;

    public boolean checkEmailByRegister(String email) {
        // 验证邮箱格式
        return loginMapper.registerUserByEmail(email) != null;
    }

    // 发送验证码
    public boolean sendVerificationCode(String email) {
        try {
            // 生成验证码
            String code = generateVerificationCode();

            // 发送邮件
            sendEmail(email, buildEmailContent(code));

            // 存储到 Redis（自动设置 5 分钟过期）
            // 执行操作（注意：这里使用底层连接的API，或通过template执行）
            redisTemplate.opsForValue().set(VerificationCode+email,code,5, TimeUnit.MINUTES);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Redis存储示例（需注入RedisTemplate）
    // 验证验证码
    public boolean validateCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(VerificationCode+email);
        if (storedCode == null || !storedCode.equals(inputCode)) {
            return false;
        }
        redisTemplate.delete(email); // 验证后删除
        return true;
    }

    // 生成随机验证码
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成6位数字验证码
        return String.valueOf(code);
    }

    // 发送邮件
    private void sendEmail(String to, String content) throws MessagingException {
        // 设置SMTP服务器属性
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // 使用TLS加密

        // 创建会话
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // 创建邮件消息
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("账户验证");
        message.setContent(content, "text/html; charset=utf-8");

        // 发送邮件
        Transport.send(message);
    }

    // 构建邮件内容
    private String buildEmailContent(String code) {
        return "<html>" +
                "<body>" +
                "<h3>您的验证码是：</h3>" +
                "<p style=\"font-size:24px;font-weight:bold;\">" + code + "</p>" +
                "<p>该验证码5分钟内有效，请尽快完成验证。</p>" +
                "<p>如果您没有请求验证，请忽略此邮件。</p>" +
                "</body>" +
                "</html>";
    }
}
