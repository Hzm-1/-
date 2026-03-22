package model.login;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.utils.EsDateTimeArrayDeserializer;
import model.utils.EsDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //用户id
    private Integer id;
    //用户名
    private String username;
    //用户性别
    private String sex;
    //密码
    private String password;
    //昵称
    private String nickname;
    //头像URL
    private String avatar;
    //电话号码
    private String phone;
    //邮箱
    private String email;
    //状态
    private Integer status;
    //创建时间
    @JsonSerialize(using = EsDateTimeSerializer.class)
    private LocalDateTime createdAt;
    //更新时间
    @JsonSerialize(using = EsDateTimeSerializer.class)
    private LocalDateTime updatedAt;
}
