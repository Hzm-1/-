package websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.chatGroup.databaseClass.ChatGroupMessage;
import model.message.CustomHeartbeatMessage;
import model.message.Message;
import model.websocket.ChatData;
import model.websocket.OnlineStatusResponseData;
import model.websocket.WebSocketMessage;
import net.bytebuddy.description.type.TypeList;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import websocket.mapper.WebSocketMapper;
import websocket.utils.WebSocketJSONHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * webSocket主处理程序
 */
@Component
@Slf4j
public class MyWsHandler extends AbstractWebSocketHandler {
    @Resource
    WebSocketJSONHandler webSocketJSONHandler;
    @Resource
    WebSocketMapper webSocketMapper;

    private static final Map<String, SessionBean> sessionBeanMap;
    private static final String SESSIONMAPKEY = "sessionId=";
    //AtomicInteger类型是Integer的线程安全类，用于保证多个线程并发访问时，数据的正确性，防止数据丢失。
    static {
        //初始化静态变量
        sessionBeanMap = new ConcurrentHashMap<>();
    }
    // 建立连接
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 创建SessionBean对象,getAndIncrement方法为获取值并自增，返回自增前的值，
        SessionBean sessionBean = new SessionBean(session, Objects.requireNonNull(session.getUri()).getQuery());
        sessionBeanMap.put(session.getUri().getQuery(), sessionBean);
        log.info("有新的连接加入，当前连接id：{}",session.getUri().getQuery());
    }

    //收到消息
    @Override
    @Transactional
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //message中存储了向某个Id发送的消息
        WebSocketMessage webSocketMessage = webSocketJSONHandler.handleTextMessage(message);
        switch (webSocketMessage.getType()){
            case "chat":
                log.info("{}", webSocketMessage);
                log.info("id: {},test: {}", webSocketMessage.getData().getReceiver(),webSocketMessage.getData().getText());
                log.info("sender:{},receiver:{}",webSocketMessage.getData().getSender(),webSocketMessage.getData().getReceiver());
                webSocketMessage.getData().setTimestamp(LocalDateTime.now());
                //log.info("{}:{}", sessionBeanMap.get(session.getId()).getClientId(), message.getPayload());

                Message msg = new Message();
                msg.setReceiverId(webSocketMessage.getData().getReceiver());
                msg.setSenderId(webSocketMessage.getData().getSender());
                msg.setMessageType(1);
                msg.setContent(webSocketMessage.getData().getText());
                msg.setSendTime(webSocketMessage.getData().getTimestamp());

                //转发消息
                if (sessionBeanMap.containsKey(SESSIONMAPKEY + webSocketMessage.getData().getReceiver())) {
                    log.info("聊天对象在线:{}，发送消息：{}", webSocketMessage.getData().getReceiver(), webSocketMessage);
                    sendToSession(webSocketMessage.getData().getReceiver(), webSocketMessage);
                    msg.setStatus(1);
                }
                log.info("接收到单人聊天消息：{}，插入数据库：{}",webSocketMessage.getData().getReceiver(),msg);
                log.info("聊天对象是否在线：{}",sessionBeanMap.containsKey(String.valueOf(webSocketMessage.getData().getReceiver())));
                webSocketMapper.insertMessage(msg);
                break;
            case "chat_group":
                log.info("接收到群聊信息：{}",webSocketMessage);
                webSocketMessage.getData().setTimestamp(LocalDateTime.now());

                ChatGroupMessage chatGroupMessage = new ChatGroupMessage();
                chatGroupMessage.setGroupId(webSocketMessage.getData().getReceiver());
                chatGroupMessage.setSenderId(webSocketMessage.getData().getSender());
                chatGroupMessage.setMessageType(1);
                chatGroupMessage.setMessageContent(webSocketMessage.getData().getText());
                chatGroupMessage.setSendTime(webSocketMessage.getData().getTimestamp());
                chatGroupMessage.setIsDeleted(0);
                chatGroupMessage.setUpdateTime(webSocketMessage.getData().getTimestamp());
                chatGroupMessage.setIsRecalled(0);

                if(sessionBeanMap.containsKey(SESSIONMAPKEY + webSocketMessage.getData().getReceiver())){
                    sendToSession(webSocketMessage.getData().getReceiver(), webSocketMessage);
                }
                webSocketMapper.insertChatGroupMessage(chatGroupMessage);
                break;
            case "ping":
                try {
                    // 创建ObjectMapper实例
                    ObjectMapper objectMapper = new ObjectMapper();
                    // 注册JavaTimeModule以支持LocalDateTime等Java 8时间类型
                    objectMapper.registerModule(new JavaTimeModule());
                    // 回复pong
                    session.sendMessage(new TextMessage(
                            objectMapper.writeValueAsString(new CustomHeartbeatMessage("pong"))
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "online_status_query":
                if(!sessionBeanMap.containsKey(SESSIONMAPKEY + webSocketMessage.getData().getReceiver())){
                    // 创建SessionBean对象,getAndIncrement方法为获取值并自增，返回自增前的值，
                    SessionBean sessionBean = new SessionBean(session, Objects.requireNonNull(session.getUri()).getQuery());
                    sessionBeanMap.put(session.getUri().getQuery(), sessionBean);
                }
                // 处理在线状态查询请求
                handleOnlineStatusQuery(session, webSocketMessage);
                break;
            default:
                log.warn("接收到未知类型的消息: {}", webSocketMessage.getType());
                break;
        }
    }

    private void handleOnlineStatusQuery(WebSocketSession session, WebSocketMessage webSocketMessage) throws Exception {
        // 1. 初始化JSON解析器（支持Java 8时间类型）
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 2. 从ChatData中解析查询参数（前端传入的friendIds和userId）
        ChatData queryData = webSocketMessage.getData();
        List<String> friendIds = queryData.getFriendIds(); // 前端传入的好友ID列表：["16", "4"]
        String currentUserId = queryData.getUserId();      // 前端传入的当前用户ID

        log.info("用户{}发起在线状态查询，需要查询的好友ID列表：{}", currentUserId, friendIds);

        // 3. 遍历friendIds，从sessionBeanMap中判断在线状态
        List<String> onlineIds = new ArrayList<>();
        List<String> offlineIds = new ArrayList<>();

        log.info("遍历好友ID列表...:{}",sessionBeanMap);
        if (friendIds != null && !friendIds.isEmpty()) {
            for (String friendId : friendIds) {
                // 关键修改：拼接key为"sessionId=XXX"的格式，匹配sessionBeanMap的key
                String mapKey = SESSIONMAPKEY + friendId;
                if (sessionBeanMap.containsKey(mapKey)) {
                    onlineIds.add(friendId); // 返给前端的还是纯数字ID，保持格式一致
                } else {
                    offlineIds.add(friendId);
                }
            }
        }

        log.info("用户{}的好友在线状态查询结果：在线{}人，离线{}人", currentUserId, onlineIds.size(), offlineIds.size());

        // 4. 构造响应的ChatData（封装在线/离线ID列表）
        ChatData responseData = new ChatData();
        responseData.setUserId(currentUserId);       // 回传当前用户ID
        responseData.setOnlineIds(onlineIds);       // 在线好友ID列表：["16"]
        responseData.setOfflineIds(offlineIds);     // 离线好友ID列表：["4"]

        // 5. 构造前端能识别的WebSocketMessage
        WebSocketMessage statusResponseMessage = new WebSocketMessage();
        statusResponseMessage.setType("online_status_response"); // 前端监听的类型
        statusResponseMessage.setData(responseData);             // 响应数据存入ChatData

        // 6. 将响应消息序列化为JSON并推送给前端
        String responseJson = objectMapper.writeValueAsString(statusResponseMessage);
        session.sendMessage(new TextMessage(responseJson));
    }

    //传输异常的情况，比如网络中断，断开连接，服务端异常
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        SessionBean sessionBean = sessionBeanMap.get(Objects.requireNonNull(session.getUri()).getQuery());
        // 从map中移除会话
        sessionBeanMap.remove(Objects.requireNonNull(session.getUri()).getQuery());
        if(session.isOpen()){
            session.close();
        }
    }

    //链接关闭
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SessionBean sessionBean = sessionBeanMap.get(Objects.requireNonNull(session.getUri()).getQuery());
        if (sessionBean != null) {
            log.info("连接关闭，当前连接id：{}", sessionBean.getClientId());
        } else {
            log.warn("连接关闭，但未找到对应的SessionBean，session id：{}", session.getId());
        }

        // 从map中移除会话
        sessionBeanMap.remove(Objects.requireNonNull(session.getUri()).getQuery());
    }

    // 向指定Session ID发送消息
    @Transactional
    public void sendToSession(Integer sessionId, WebSocketMessage webSocket) {
        WebSocketSession session = sessionBeanMap.get(SESSIONMAPKEY + sessionId).getWebSocketSession();
        if (session != null && session.isOpen()) {
            try {
                // 将对象序列化为JSON字符串
                String jsonMessage;
                try {
                    // 创建ObjectMapper并配置时间序列化
                    ObjectMapper objectMapper = new ObjectMapper();
                    JavaTimeModule timeModule = new JavaTimeModule();

                    // 自定义LocalDateTime序列化格式（例如：2025-10-17 14:43:02.292）
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                    timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

                    objectMapper.registerModule(timeModule);
                    // 关闭默认的日期时间作为数组序列化的功能（兼容旧版本Jackson）
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                    jsonMessage = objectMapper.writeValueAsString(webSocket);
                    // 发送JSON消息
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (JsonProcessingException e) {
                    log.error("对象序列化JSON失败", e);
                    return;
                }
                log.info("已向Session {} 发送JSON消息: {}", sessionId, jsonMessage);
            } catch (IOException e) {
                log.info("Session {} 不存在或连接已关闭", sessionId);
            }
        }
    }
}
