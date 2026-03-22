package rabbitMQ.conf;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * PostConstruct注解是Java中的一个标准注解，它用于指定在对象创建之后立即执行的方法，当使用依赖注入（如Spring框架）或者其他方式创建对象时
     * PostConstruct注解可以确保在对象完全初始化之后，执行相应的方法。
     *
     * 使用@PostConstruct的方法必须满足以下条件：
     *  1、方法不能有任何参数
     *  2、方法必须是非静态的
     *  3、方法不能返回任何值
     *
     *  当容器实列化一个带有@PostConstruct注解的Bean时，它会在调用构造函数之后，并在依赖注入完成之前调用被@PostConstruct注解标记的方法
     *  这样，我们可以在该方法中进行一些初始化操作。比如读取配置文件，建立数据库链接等。
     */

    /**
     * RabbitTemplate的增强方法：
     *      因为回调函数所在对象必须设置到RabbitTemplate对象中才能生效。
     *      原本RabbitTemplate对象并没有生产者端消息确认的功能，要给它设置对应的组件才可以。
     *      而设置对应的组件，需要调用RabbitTemplate对象下面的两个方法：
     *          setConfirmCallback() 需要ConfirmCallback接口类型
     *          setReturnsCallback() 需要ReturnsCallback接口类型
     *
     * 使用@PostConstruct注解标记该方法，意味着在当前 Bean 的依赖注入完成后会自动执行此初始化方法。
     * 通过rabbitTemplate.setConfirmCallback(this)将当前对象设置为消息发送确认的回调处理器，用于处理：
     *      消息是否成功发送到 RabbitMQ 服务器的交换机（Exchange）
     *      可以通过实现ConfirmCallback接口的confirm()方法来处理确认结果
     * 通过rabbitTemplate.setReturnsCallback(this)将当前对象设置为消息返回的回调处理器，用于处理：
     *      消息成功发送到交换机，但无法路由到队列的情况
     *      可以通过实现ReturnsCallback接口的returnedMessage()方法来处理返回的消息
     */
    @PostConstruct
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 确认消息是否发送到交换机
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 消息发送到交换机成功或者失败以后都会调用该方法
        log.info("confirm() 回调函数打印CorrelationData ：{}", correlationData);
        log.info("confirm() 回调函数打印ack ：{}", ack);
        log.info("confirm() 回调函数打印cause ：{}", cause);
    }

    /**
     * 确认消息是否发送到队列
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        // 发送到队列失败时才回调这个方法
        log.info("returnedMessage() 回调函数 消息主体：{}", new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回调函数 应答码：{}", returnedMessage.getReplyCode());//响应状态码
        log.info("returnedMessage() 回调函数 描述：{}", returnedMessage.getReplyText());//响应状态码的描述
        log.info("returnedMessage() 回调函数 消息使用的交换机器 exchange：{}", returnedMessage.getExchange());
        log.info("returnedMessage() 回调函数 消息使用的路由键 routing：{}", returnedMessage.getRoutingKey());
    }
}
