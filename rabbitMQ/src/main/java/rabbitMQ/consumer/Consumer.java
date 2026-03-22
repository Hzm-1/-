package rabbitMQ.consumer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import model.chatGroup.databaseClass.ChatGroup;
import model.login.User;
import model.websocket.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.*;
import org.springframework.web.socket.TextMessage;
import rabbitMQ.feign.ElasticsearchFeignClient;
import rabbitMQ.feign.WebSocketFeignClient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class Consumer {
    // RabbitMQ 配置

    @Value("${spring.rabbitmq.host:127.0.0.1}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port:5672}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username:guest}")
    private String rabbitmqUser;

    @Value("${spring.rabbitmq.password:guest}")
    private String rabbitmqPass;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String vhost;

    private static final String EXCHANGE_NAME = "ChatDemo.main";
    private static final String EXCHANGE_NAME_ES = "ChatDemo.Elasticsearch";
    private static final String VHOST = "/";
    private final Map<String, String> queueStatus = new ConcurrentHashMap<>();

    private HttpClient httpClient;
    private String authHeader;
    private Connection connection;
    private Channel channel;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        JavaTimeModule module = new JavaTimeModule();
        // 配置LocalDateTime反序列化格式（匹配消息中的时间字符串）
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        objectMapper.registerModule(module);
    }

    @Autowired
    private WebSocketFeignClient webSocketFeignClient;
    @Autowired
    private ElasticsearchFeignClient elasticsearchFeignClient;


    // 构造函数现在什么都不做
    public Consumer() {
        log.info("Consumer bean created");
    }

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing RabbitMQ connection to {}:{} with user {}",
                    rabbitmqHost, rabbitmqPort, rabbitmqUser);

            this.httpClient = HttpClient.newHttpClient();

            // 创建管理API的认证头
            String auth = rabbitmqUser + ":" + rabbitmqPass;
            this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            // 建立RabbitMQ连接和通道
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitmqHost);
            factory.setPort(rabbitmqPort);
            factory.setUsername(rabbitmqUser);
            factory.setPassword(rabbitmqPass);
            factory.setVirtualHost(vhost);

            // 设置超时时间
            factory.setConnectionTimeout(30000);
            factory.setHandshakeTimeout(30000);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(10000);

            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            log.info("RabbitMQ connection established successfully");
        } catch (Exception e) {
            log.error("Failed to initialize RabbitMQ connection", e);
            throw new RuntimeException("RabbitMQ initialization failed", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            close();
        } catch (Exception e) {
            log.error("Error during cleanup", e);
        }
    }
    /**
     * 消费指定队列中的所有消息
     */
    public void consumeQueueMessages(String queueName) {
        try {
            // 1. 验证交换机是否存在（被动声明）
            channel.exchangeDeclarePassive(EXCHANGE_NAME);
            log.info("队列名称：{}",queueName);

            // 2. 验证队列是否存在并获取队列状态
            AMQP.Queue.DeclareOk queueDeclareOk = channel.queueDeclarePassive(queueName);

            // 4. 创建消费者并开始消费消息
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    TextMessage jsonMessage = new TextMessage(body);
                    log.info("收到JSON消息: {}", jsonMessage);

                    WebSocketMessage webSocketMessage = objectMapper.readValue(jsonMessage.getPayload(), WebSocketMessage.class);

                    log.info("收到消息: {}", jsonMessage.getPayload());
                    log.info("路由键: {}", envelope.getRoutingKey());
                    log.info("交换机: {}", envelope.getExchange());
                    log.info("{}",webSocketMessage);
                    log.info("webSocketMessage:{}",webSocketMessage);

                    webSocketFeignClient.sendToSession(webSocketMessage.getData().getReceiver(),webSocketMessage);
                    // 手动确认消息
                    channel.basicAck(envelope.getDeliveryTag(), false);

                }
            };
            // 开始消费消息
            String consumerTag = channel.basicConsume(queueName, false, consumer);
            queueStatus.put(queueName, consumerTag);
            log.info("队列 {} 的消费者已启动", queueStatus.get(queueName));
            log.info("开始消费队列 {} 的消息，consumerTag: {}", queueName, consumerTag);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUserOfEs(String queueName){
        try {
            // 1. 验证交换机是否存在（被动声明）
            channel.exchangeDeclarePassive(EXCHANGE_NAME_ES);

            // 4. 创建消费者并开始消费消息
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    TextMessage jsonMessage = new TextMessage(body);

                    log.info("收到消息: {}", jsonMessage.getPayload());

                    User user = objectMapper.readValue(jsonMessage.getPayload(), User.class);


                    log.info("路由键: {}", envelope.getRoutingKey());
                    log.info("交换机: {}", envelope.getExchange());
                    log.info("{}",user);
                    log.info("user:{}",user);

                    elasticsearchFeignClient.updateUser(user);
                    // 手动确认消息
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            // 开始消费消息
            String consumerTag = channel.basicConsume(queueName, false, consumer);
            queueStatus.put(queueName, consumerTag);
            log.info("开始消费队列 {} 的消息，consumerTag: {}", queueName, consumerTag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为ES消费用户信息
     * @param queueName
     * @throws TimeoutException
     */
    public void consumeQueueMessagesOfES(String queueName) throws TimeoutException {
        try {
            // 1. 验证交换机是否存在（被动声明）
            channel.exchangeDeclarePassive(EXCHANGE_NAME_ES);

            // 4. 创建消费者并开始消费消息
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    TextMessage jsonMessage = new TextMessage(body);

                    log.info("收到消息: {}", jsonMessage.getPayload());

                    User user = objectMapper.readValue(jsonMessage.getPayload(), User.class);


                    log.info("路由键: {}", envelope.getRoutingKey());
                    log.info("交换机: {}", envelope.getExchange());
                    log.info("{}",user);
                    log.info("user:{}",user);

                    elasticsearchFeignClient.consumerEs(user);
                    // 手动确认消息
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            // 开始消费消息
            String consumerTag = channel.basicConsume(queueName, false, consumer);
            queueStatus.put(queueName, consumerTag);
            log.info("开始消费队列 {} 的消息，consumerTag: {}", queueName, consumerTag);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 为ES消费群聊信息
     * @param queueName
     * @throws TimeoutException
     */
    public void consumeQueueMessagesOfESToGroup(String queueName) throws TimeoutException {
        try {
            // 1. 验证交换机是否存在（被动声明）
            channel.exchangeDeclarePassive(EXCHANGE_NAME_ES);

            // 4. 创建消费者并开始消费消息
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    TextMessage jsonMessage = new TextMessage(body);

                    log.info("收到消息: {}", jsonMessage.getPayload());

                    ChatGroup group = objectMapper.readValue(jsonMessage.getPayload(), ChatGroup.class);


                    log.info("路由键: {}", envelope.getRoutingKey());
                    log.info("交换机: {}", envelope.getExchange());
                    log.info("{}",group);
                    log.info("group:{}",group);

                    elasticsearchFeignClient.consumerEsToGroup(group);
                    // 手动确认消息
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            // 开始消费消息
            String consumerTag = channel.basicConsume(queueName, false, consumer);
            queueStatus.put(queueName, consumerTag);
            log.info("开始消费队列 {} 的消息，consumerTag: {}", queueName, consumerTag);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭连接资源
     */
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                log.info("Channel closed");
            }
        } catch (Exception e) {
            log.error("Error closing channel", e);
        }

        try {
            if (connection != null && connection.isOpen()) {
                connection.close();
                log.info("Connection closed");
            }
        } catch (Exception e) {
            log.error("Error closing connection", e);
        }
    }

    /**
     * 关闭当前消费者（通过consumerTag取消消费）
     */
    public void cancelCurrentConsumer(String queueName) {
        String currentConsumerTag = queueStatus.get(queueName);
        log.info("当前队列: {}", queueName);
        log.info("当前消费者Tag: {}", currentConsumerTag);
        if (currentConsumerTag != null && channel != null && channel.isOpen()) {
            try {
                // 取消消费者
                channel.basicCancel(currentConsumerTag);
                log.info("已关闭消费者，consumerTag: {}", currentConsumerTag);
            } catch (IOException e) {
                log.error("关闭消费者失败", e);
            }
        } else {
            log.warn("没有可关闭的消费者（可能未初始化或已关闭）");
        }
    }

}
