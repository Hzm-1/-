package elasticsearch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "rabbitMQ")
public interface RabbitMQFeignClient {

    @GetMapping("/consumeQueueMessagesOfES")
    public void consumeQueueMessagesOfES(@RequestParam("queueName") String queueName);
}
