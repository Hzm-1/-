package model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatData {
    //接收消息的接收者id
    private Integer receiver;
    //发送消息的发送者id
    private Integer sender;
    private String text; // 消息内容
    private LocalDateTime timestamp; // 时间戳（毫秒级）

    // 新增在线状态查询/响应相关字段
    private String userId;       // 当前发起查询的用户ID
    private List<String> friendIds; // 需要查询的好友ID列表
    private List<String> onlineIds; // 在线的好友ID列表
    private List<String> offlineIds; // 离线的好友ID列表

    public ChatData(Integer receiver,Integer sender, String text) {
        this.receiver = receiver;
        this.sender = sender;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }
}