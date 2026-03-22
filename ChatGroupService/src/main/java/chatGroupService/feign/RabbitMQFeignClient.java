package chatGroupService.feign;

import chatGroupService.feign.fallback.RabbitMQFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "rabbitMQ",url = "http://8.145.60.4:8082", fallback = RabbitMQFeignClientFallback.class)
public interface RabbitMQFeignClient {

    @GetMapping("/createQueue")
    public void createQueue(@RequestParam("email") String email);

    @PutMapping("/sendEsTask")
    public void sendEsTask(@RequestParam("exchange") String exchange,@RequestParam("routingKey") String routingKey,@RequestParam("data") String data);
}
