package rabbitMQ.controller;

import lombok.extern.slf4j.Slf4j;
import model.login.User;
import model.message.EsTask;
import model.websocket.WebSocketMessage;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;
import rabbitMQ.consumer.Consumer;
import rabbitMQ.create.CreateQueue;
import rabbitMQ.producer.Producer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class RabbitMQController {
    @Autowired
    private Consumer consumer;

    @Autowired
    private Producer producer;

    @Autowired
    private CreateQueue createQueue;

    @PostMapping("/sendMsg")
    public void sendMsg(@RequestParam("exchange") String exchange,@RequestParam("routingKey") String routingKey,@RequestBody WebSocketMessage webSocketMessage) {
    	producer.sendMsg(exchange, routingKey, webSocketMessage);
    }
    @GetMapping("/consumeQueueMessages")
    public void consumeQueueMessages(@RequestParam("queueName") String queueName) {
        consumer.consumeQueueMessages(queueName);
    }

    @GetMapping("/createQueue")
    public void createQueue(@RequestParam("email") String email) {
        createQueue.createQueue(email);
    }

    @PutMapping("/sendEsTask")
    public void sendEsTask(@RequestParam("exchange") String exchange,@RequestParam("routingKey") String routingKey,@RequestParam("data") String data) {
    	producer.sendMsg(exchange, routingKey, data);
    }

    @GetMapping("/consumeQueueMessagesOfES")
    public void consumeQueueMessagesOfES(@RequestParam("queueName") String queueName) throws TimeoutException {
        consumer.consumeQueueMessagesOfES(queueName);
    }

    @GetMapping("/cancelCurrentConsumer")
    public void cancelCurrentConsumer(@RequestParam("queueName") String queueName) {
        consumer.cancelCurrentConsumer(queueName);
    }

    @GetMapping("/consumeUpdateUserOfEs")
    public void updateUserOfEs(@RequestParam("queueName") String queueName) {
    	try{
            consumer.updateUserOfEs(queueName);
        }catch (Exception e){
            log.error("更新用户信息失败",e);
        }
    }

    @PostMapping("/produceUpdateUserOfEs")
    public void produceUpdateUserOfEs(@RequestParam("exchange") String exchange,@RequestParam("routingKey") String routingKey,@RequestBody User user) {
        producer.produceUpdateUserOfEs(exchange, routingKey, user);
    }
}
