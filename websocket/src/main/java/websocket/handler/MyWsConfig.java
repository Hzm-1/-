package websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import websocket.interceptor.MyWsInterceptor;

@Configuration
@EnableWebSocket
public class MyWsConfig implements WebSocketConfigurer {

    @Autowired
    MyWsHandler myWsHandler;
    @Autowired
    MyWsInterceptor myWsInterceptor;

    //注册Handler
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //setAllowedOrigins指谁来进行websocket链接，*为任意，也可以是任意地址
        registry.addHandler(myWsHandler,"/ws").addInterceptors(myWsInterceptor).setAllowedOrigins("*");
    }
}
