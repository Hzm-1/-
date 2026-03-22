package model.chatGroup.databaseClass;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatGroup {

    //群组id
    private Integer groupId;

    //群聊名称
    private String groupName;

    //群聊头像
    private String groupAvatar;

    //群聊创建人id
    private Integer creatorId;

    //群聊描述
    private String description;

    //群最大人数
    private Integer maxMember;

    //群状态（1-正常，2-解散，3-封禁）
    private Integer status;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}
