package chatGroupService.controller;

import chatGroupService.service.ChatGroupService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.chatGroup.VO.ChatGroupVO;
import model.chatGroup.databaseClass.ChatGroup;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/chatGroup")
public class ChatGroupController {
    @Resource
    private ChatGroupService chatGroupService;

    @PostMapping("/createChatGroup")
    public void createChatGroup(@RequestBody ChatGroupVO chatGroup) throws JsonProcessingException {
        log.info("创建群组:{}", chatGroup);
        chatGroupService.createChatGroup(chatGroup);
    }
}
