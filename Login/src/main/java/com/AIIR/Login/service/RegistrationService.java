package com.AIIR.Login.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import model.login.User;


public interface RegistrationService {
    void register(User user) throws JsonProcessingException;
}
