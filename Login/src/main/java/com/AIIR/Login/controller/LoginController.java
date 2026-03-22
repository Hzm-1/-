package com.AIIR.Login.controller;


import com.AIIR.Login.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import model.login.Result;
import model.login.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public Result login(String email, String password) {
        Result result = new Result();
        log.info("用户名：{}，密码：{}", email, password);
        UserVO user = loginService.login(email, password);
        result.setUser(user);
        if(user != null){
            result.setToken(String.valueOf(user.getToken()));
        }else{
            result.setToken(null);
            log.info("用户名或密码错误");
        }
        return result;
    }

//    @GetMapping("/admin/login")
//    public Result adminLogin(String adminId, String password) {
//        Admin admin = loginService.adminLogin(adminId, password);
//        if (admin != null) {
//            return Result.success(admin);
//        } else {
//            return Result.error("账号或密码错误");
//        }
//    }
}
