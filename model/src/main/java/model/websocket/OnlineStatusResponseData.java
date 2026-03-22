package model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 在线状态响应的结果数据实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineStatusResponseData {
    // 在线的好友ID列表
    private List<String> onlineIds;
    // 离线的好友ID列表
    private List<String> offlineIds;
}
