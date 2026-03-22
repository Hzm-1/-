package model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    //消息id
    private Integer id;
    //会话id（接收者id）
    private Integer receiverId;
    //发送者id
    private Integer senderId;
    //消息类型（1-文本，2-图片，3-语音，4-文件）
    private Integer messageType;
    // 内容
    private String content;
    //文件名（如果是文件）
    private String fileName;
    //文件大小（如果是文件）
    private long fileSize;
    //文件路径（如果是文件）
    private String filePath;
    //消息状态（1-正常，0-撤回，2-删除）
    private Integer status=1;
    //发送时间
    private LocalDateTime sendTime;
    //阅读状态（1-已读，0-未读）
    private Integer readStatus=0;
}
