package chatGroupService.feign.fallback;

import chatGroupService.feign.RabbitMQFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQFeignClientFallback implements RabbitMQFeignClient {

    @Override
    public void createQueue(String email) {

    }

    @Override
    public void sendEsTask(String exchange, String routingKey, String data) {
        try {
            // 尝试重新发送任务或存储到本地队列
            log.warn("尝试重新发送任务到 {}，routingKey: {}, data: {}", exchange, routingKey, data);
            // 这里可以添加重试逻辑或存储到本地队列的代码
        } catch (Exception e) {
            log.error("发送rabbitMQ向Elasticsearch的远程调用失败，exchange: {}, routingKey: {}, data: {}", exchange, routingKey, data, e);
            // 抛出自定义异常或记录错误信息
            throw new RuntimeException("发送rabbitMQ向Elasticsearch的远程调用失败", e);
        }
    }
}
