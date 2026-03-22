package rabbitMQ.feign;

import model.websocket.WebSocketMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "websocket")
public interface WebSocketFeignClient {

    @PostMapping("/sendToSession")
    public void sendToSession(@RequestParam("sessionId") Integer sessionId, @RequestBody WebSocketMessage message);

}
