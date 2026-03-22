package model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 在线状态查询的请求数据实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineStatusQueryData {
    // 当前请求的用户ID
    private String userId;
    // 需要查询的好友ID列表
    private List<String> friendIds;
}
