package rabbitMQ;

import feign.FeignException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import rabbitMQ.consumer.Consumer;

@SpringBootApplication
@EnableDiscoveryClient //在每个微服务中都要添加该注解
@EnableFeignClients(basePackages = "rabbitMQ.feign")
@Slf4j
public class RabbitMQApplication {
    @Resource
    private Consumer consumer;
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQApplication.class, args);
    }

    /**
     * ApplicationRunner是一个一次性任务，只要服务启动起来这个任务就会启动
     */
    @Bean
    ApplicationRunner applicationRunner(){
        return args -> {
            log.info("一次性任务启动");
            try {
                consumer.consumeQueueMessagesOfES("queue.elasticsearch");
                consumer.consumeQueueMessagesOfESToGroup("queue.elasticsearch.group");
                consumer.updateUserOfEs("queue.elasticsearch.setting");
            } catch (FeignException.ServiceUnavailable e) {
                log.error("无法连接到 rabbitMQ 服务，请检查服务状态: {}", e.getMessage());
                // 可以选择记录日志或执行其他补偿操作
            }
        };
    }
}
