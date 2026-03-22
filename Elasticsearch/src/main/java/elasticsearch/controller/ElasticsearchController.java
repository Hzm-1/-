package elasticsearch.controller;

import elasticsearch.service.UserWithMQService;
import elasticsearch.synchronization.ElasticsearchSynchronization;
import jakarta.annotation.Resource;
import model.chatGroup.databaseClass.ChatGroup;
import model.login.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/Es")
public class ElasticsearchController {

    @Resource
    private ElasticsearchSynchronization elasticsearchSynchronization;

    @Resource
    private UserWithMQService userWithMQService;

    @GetMapping("/user/search")
    public List<User> searchUser(@RequestParam("email") String email) throws IOException {
        return userWithMQService.searchUser(email);
    }

    @GetMapping("/group/search")
    public String searchGroup(@RequestParam("name") String name) {
        return "Hello, World!";
    }

    @PostMapping("/user/synchronization")
    public void consumerEs(@RequestBody User users) throws IOException {
        elasticsearchSynchronization.synchronizeUser(users);
    }

    @PostMapping("/user/synchronizeGroup")
    public void consumerEsToGroup(@RequestBody ChatGroup group) throws IOException {
        elasticsearchSynchronization.synchronizeGroup(group);
    }

    @PostMapping("/user/update")
    public void updateUser(@RequestBody User user) throws IOException {
    	elasticsearchSynchronization.updateUser(user);
    }
}
