package websocket.controller;

import model.websocket.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import websocket.handler.MyWsHandler;


@RestController
public class WebSocketController {
    @Autowired
    private MyWsHandler myWsHandler;

    @PostMapping("/sendToSession")
    public void sendToSession(@RequestParam("sessionId") Integer sessionId, @RequestBody WebSocketMessage message) {
        myWsHandler.sendToSession(sessionId, message);
    }
}
