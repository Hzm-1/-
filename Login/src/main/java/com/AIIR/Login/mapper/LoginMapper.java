package com.AIIR.Login.mapper;

import model.login.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface LoginMapper {

    /**
     * 根据用户id和password登录
     */
    @Select("select * from user where email=#{email} and password=#{password}")
    User loginByEmailPassword(String email, String password);

    /**
     * 根据邮箱注册账号
     */
    @Insert("insert into user(username,password,nickname,avatar,phone,email,status,created_at,updated_at) values(#{username},#{password},#{nickname},#{avatar},#{phone},#{email},#{status},#{createdAt},#{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 关键配置
    void registerByEmail(User user);

    /**
     * 修改用户的密码
     */
    @Update("update user set password=#{password} where email=#{email}")
    void updatePassword(String email, String password);

    /**
     * 注册时判断邮箱是否存在
     */
    @Select("select * from user where email= #{email}")
    User registerUserByEmail(String email);

    /**
     * 管理员登录
     */
//    @Select("select * from admins where admin_id=#{adminId} and password_hash=#{password}")
//    com.AIIR.Login.pojo.Admin adminLogin(String adminId, String password);
}
