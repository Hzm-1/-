package model.chatGroup.databaseClass;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatGroupUser {
    //id
    private Integer id;

    //群id
    private Integer groupId;

    //群聊成员id
    private Integer userId;

    //成员角色（1-群主，2-管理员，3-普通成员）
    private Integer role;

    //成员状态（1-正常在群，2-主动退出，3-被管理员踢出群）
    private Integer status;

    //加入群聊时间
    private LocalDateTime joinTime;

    //退出群聊时间
    private LocalDateTime quitTime;

    //创建时间
    private LocalDateTime createdTime;

    //更新时间
    private LocalDateTime updatedTime;
}
