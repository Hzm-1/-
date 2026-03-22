package com.AIIR.Login.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "rabbitMQ")
public interface RabbitMQFeignClient {

    @GetMapping("/createQueue")
    public void createQueue(@RequestParam("email") String email);

    @PutMapping("/sendEsTask")
    public void sendEsTask(@RequestParam("exchange") String exchange,@RequestParam("routingKey") String routingKey,@RequestParam("data") String data);
}
