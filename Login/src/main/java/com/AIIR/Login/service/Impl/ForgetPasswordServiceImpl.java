package com.AIIR.Login.service.Impl;

import com.AIIR.Login.mapper.LoginMapper;
import com.AIIR.Login.service.ForgetPasswordService;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public void forgetPassword(User user) {
        loginMapper.updatePassword(user.getPassword(),user.getEmail());
    }
}
