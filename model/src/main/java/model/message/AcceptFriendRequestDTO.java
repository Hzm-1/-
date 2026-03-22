package model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 好友申请同意的请求封装类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptFriendRequestDTO {
    private Person person1;
    private Person person2;
}