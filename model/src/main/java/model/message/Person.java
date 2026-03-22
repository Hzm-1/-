package model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据库中对应friend表的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    //id
    private Integer id;
    //用户id
    private Integer userId;
    //好友id
    private Integer friendId;
    //好友昵称
    private String friendNickname;
    //好友状态（1-正常，2-冻结）
    private Integer status;
    //创建时间
    private LocalDateTime createdAt;
    //更新时间
    private LocalDateTime updatedAt;
    //最后一条消息
    private Message lastMessage;
    //未读消息数
    private Integer unreadCount;
    //好友分类
    private String category;
}
