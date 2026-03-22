package model.chatGroup.databaseClass;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatGroupMessage {
    //记录id
    private Integer recordId;

    //群id
    private Integer groupId;

    //发送者id
    private Integer senderId;

    //消息类型（1-文本，2-图片，3-语言，4-文件，5-表情）
    private Integer messageType;

    //消息内容
    private String messageContent;

    //是否删除
    private Integer isDeleted;

    //是否撤回
    private Integer isRecalled;

    //发送时间
    private LocalDateTime sendTime;

    //更新时间
    private LocalDateTime updateTime;
}
