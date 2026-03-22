package websocket.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Data
public class SessionBean {
    //spring封装的用户session
    private WebSocketSession webSocketSession;
    //用户Id
    private String clientId;
}
