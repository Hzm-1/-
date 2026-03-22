package websocket.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.websocket.WebSocketMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class WebSocketJSONHandler {
    // Jackson的JSON解析器
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketJSONHandler() {
        // 注册JavaTimeModule以支持LocalDateTime等Java 8时间类型
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 处理这类格式数据
     * {
     *     type: 'chat',
     *     data: {
     *       sender:selectedContactId.value,
     *       text: text
     *     }
     *   }
     */
    public WebSocketMessage handleTextMessage(TextMessage message) throws Exception {
        // 1. 获取前端发送的JSON字符串
        String jsonPayload = message.getPayload();

        // 2. 将JSON字符串解析为WebSocketMessage对象
        return objectMapper.readValue(jsonPayload, WebSocketMessage.class);
    }
}
