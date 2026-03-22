package com.AIIR.Login.service;




import model.login.User;
import model.login.UserVO;
import model.message.Message;

import java.math.BigInteger;
import java.util.List;

public interface LoginService {

    UserVO login(String email, String password);

//    Admin adminLogin(String adminId, String password);
}
