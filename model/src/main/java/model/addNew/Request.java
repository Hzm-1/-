package model.addNew;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    //记录id
    private Integer id;
    //发起请求的用户email
    private String fromUserEmail;
    //发起请求的用户id
    private Integer fromUserId;
    //被请求的用户email
    private String toUserEmail;
    //被请求的用户id
    private Integer toUserId;
    //设置的备注信息
    private String remark;
    //设置的好友分组
    private String friendGroup;
    //请求状态,0-未处理，1-同意，2-拒绝
    private Integer status;
    //请求内容
    private String message;
    //请求时间
    private LocalDateTime createdAt;
    //处理时间
    private LocalDateTime updatedAt;
}
