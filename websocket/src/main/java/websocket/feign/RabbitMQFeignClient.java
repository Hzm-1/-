package websocket.feign;

import model.websocket.WebSocketMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "rabbitMQ")
public interface RabbitMQFeignClient {
    //当方法有多个参数时，必须明确指定每个参数的用途，如使用 @RequestParam、@PathVariable 等注解。
    @PostMapping("/sendMsg")
    public void sendMsg(@RequestParam("exchange") String exchange,@RequestParam("routingKey") String routingKey,@RequestBody WebSocketMessage webSocketMessage);

    @GetMapping("/consumeQueueMessages")
    public void consumeQueueMessages(@RequestParam("queueName") String queueName);

    @GetMapping("/cancelCurrentConsumer")
    public void cancelCurrentConsumer(@RequestParam("queueName") String queueName);
}
