package rabbitMQ.create;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class CreateQueue {
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;
    // 交换机名称
    private static final String EXCHANGE_NAME = "ChatDemo.main";
    Connection connection = null;
    Channel channel = null;
    public void createQueue(String id) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host); // 或者 RabbitMQ 服务器地址
            factory.setPort(port);        // RabbitMQ 默认端口
            factory.setUsername(username); // 默认用户名
            factory.setPassword(password); // 默认密码
            factory.setVirtualHost(virtualHost);  // 默认虚拟主机
            // 建立连接
            connection = factory.newConnection();
            // 创建通道
            channel = connection.createChannel();

            // 声明队列
            // 参数说明：队列名称、是否持久化、是否排他、是否自动删除、其他参数
            channel.queueDeclare(id, true, false, false, null);

            // 将队列绑定到交换机
            // 参数说明：队列名称、交换机名称、路由键、其他参数
            channel.queueBind(id, EXCHANGE_NAME, id, null);

            log.info("队列已成功绑定到交换机");

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (channel != null) channel.close();
                if (connection != null) connection.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
