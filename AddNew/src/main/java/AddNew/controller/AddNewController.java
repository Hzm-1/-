package AddNew.controller;

import AddNew.service.AddNewUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.addNew.Request;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addNew")
@Slf4j
public class AddNewController {
    @Resource
    private AddNewUserService addNewUserService;

    @PostMapping("/user")
    public boolean addNewUser(@RequestBody Request request) {
        if(addNewUserService.ifExistFriend(request)){
            return false;
        }
        log.info("addNew/user:添加新的用户:{}", request);
        addNewUserService.addNewUser(request);
        return true;
    }

}
