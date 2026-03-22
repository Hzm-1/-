package model.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

// 自定义心跳消息类，命名避免与框架类重复
@Data
@NoArgsConstructor
public class CustomHeartbeatMessage {
    private String type; // 用于区分 ping/pong
    
    // 公开的构造方法
    public CustomHeartbeatMessage(String type) {
        this.type = type;
    }
}