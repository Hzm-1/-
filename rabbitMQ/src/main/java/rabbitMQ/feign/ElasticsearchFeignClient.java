package rabbitMQ.feign;

import model.chatGroup.databaseClass.ChatGroup;
import model.login.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(value = "Elasticsearch")
public interface ElasticsearchFeignClient {

    @PostMapping("/Es/user/synchronization")
    public void consumerEs(@RequestBody User users);

    @PostMapping("/Es/user/update")
    public void updateUser(@RequestBody User user);

    @PostMapping("/Es/user/synchronizeGroup")
    public void consumerEsToGroup(@RequestBody ChatGroup group);
}
