package model.AIChatModel;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AIChatMessage {
    //消息id
    private Integer messageId;
    //AI的id
    private Integer aiId;
    //聊天会话id（同一轮会话唯一标识）
    private String chatSessionId;
    //关联用户id
    private Integer userId;
    //消息角色（USER/AI/SYSTEM）
    private String messageRole;
    //消息内容
    private String messageContent;
    //消息类型（TEXT-文本，IMAGE-图片，FILE-文件）
    private String messageType;
    //消息发送时间
    private LocalDateTime sendTime;
    //消息状态（1-成功，0-失败）
    private Integer messageStatus;
    //错误信息
    private String errorMsg;
    //消息消耗的token数
    private Integer tokenUsage;
    //扩展信息（JSON格式）
    private String extInfo;
}
