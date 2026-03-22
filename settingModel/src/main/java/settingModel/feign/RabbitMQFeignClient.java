package settingModel.feign;

import model.login.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "rabbitMQ")
public interface RabbitMQFeignClient {
    @PostMapping("/produceUpdateUserOfEs")
    public void produceUpdateUserOfEs(@RequestParam("exchange") String exchange, @RequestParam("routingKey") String routingKey, @RequestBody User user);
}
