package com.AIIR.Login.service.Impl;

import com.AIIR.Login.mapper.LoginMapper;

import com.AIIR.Login.service.LoginService;
import com.AIIR.Login.utils.RedisOperate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import model.login.UserVO;
import model.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;
    @Resource
    private RedisOperate redisOperate;



    @Override
    public UserVO login(String email, String password) {
        User user = loginMapper.loginByEmailPassword(email,password);
        UserVO userVO = null;
        //缓存登录信息
        if(user != null){
            userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",user.getId());
            userVO.setToken(JwtUtil.generateJwt(claims));
            log.info("User:{}",user);
            log.info("获取token:{}",JwtUtil.generateJwt(claims));
            redisOperate.setRedisLogin(userVO);
        }
        return userVO;
    }

//    @Override
//    public Admin adminLogin(String adminId, String password) {
//        return loginMapper.adminLogin(adminId, password);
//    }
}
