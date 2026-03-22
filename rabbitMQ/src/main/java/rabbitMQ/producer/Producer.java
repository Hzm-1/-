package rabbitMQ.producer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import model.websocket.WebSocketMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "ChatDemo.main";

    static ObjectMapper objectMapper = new ObjectMapper();
    static {
        // 注册 JavaTimeModule 以支持 LocalDateTime 等时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }
    /**
     * 根据交换机，路由键，消息发送消息
     */
    public void sendMsg(String exchange, String routingKey, WebSocketMessage webSocketMessage) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(webSocketMessage);
            rabbitTemplate.convertAndSend(exchange, routingKey, jsonMessage);
        } catch (Exception e) {
            // 处理序列化异常
            e.printStackTrace();
        }
    }

    public void sendMsg(String exchange, String routingKey, String data) {
        try {
            log.info("发送数据：{}", data);
            // 3. 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    exchange,    // 交换机
                    routingKey,      // 路由键
                    data
            );
        } catch (Exception e) {
            // 5. 消息发送失败：事务回滚（MySQL和消息表插入都会撤销）
            log.error("RabbitMQ消息发送失败", e);
            throw new RuntimeException("创建用户失败", e);
        }
    }

    public void produceUpdateUserOfEs(String exchange, String routingKey, User user){
        try {
            log.info("发送更新数据：{}", user);
            String jsonMessage = objectMapper.writeValueAsString(user);
            // 3. 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    exchange,    // 交换机
                    routingKey,      // 路由键
                    jsonMessage
            );
        } catch (Exception e) {
            // 5. 消息发送失败：事务回滚（MySQL和消息表插入都会撤销）
            log.error("RabbitMQ消息发送失败", e);
            throw new RuntimeException("创建用户失败", e);
        }
    }
}
