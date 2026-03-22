package model.addNew;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.login.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestReceiver {

    //发送请求的用户信息
    private User user;
    //发送请求携带的留言
    private String message;
    //发送请求挈带的备注
    private String remark;
    //发送请求的状态
    private Integer status;
}
