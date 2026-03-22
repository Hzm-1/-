package message.controller;

import lombok.extern.slf4j.Slf4j;
import message.service.MessageService;
import model.AIChatModel.AIChatMessage;
import model.AIChatModel.AIMessageQueryParam;
import model.chatGroup.databaseClass.ChatGroupMessage;
import model.chatGroup.databaseClass.GroupMessageQueryParam;
import model.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取历史，在第一次之后聊天消息,获取已读消息
     */
    @PostMapping("/messages/pull")
    public Map<String, Object> historyMessages(@RequestBody MessageQueryParam messageQueryParam){
        log.info("messages/pull:获取历史，在第一次之后聊天消息:{}", messageQueryParam);
        Map<String, Object> result = new HashMap<>();
        messageService.updateMessage(messageQueryParam.getReceiverId());
        PageResult<Message> pageResult = messageService.historyMessages(messageQueryParam);
        result.put("message",pageResult);
        log.info("messages/pull:获取历史，在第一次之后聊天消息成功:{}", pageResult);
        return result;
    }

    /**
     * 获取AI聊天历史信息，获取同一轮的聊天消息
     */
    @PostMapping("/AIChatMessagesBySessionId/pull")
    public Map<String, Object> AIHistoryMessages(@RequestBody AIMessageQueryParam aiMessageQueryParam){
        log.info("AIChatMessagesBySessionId/pull:获取历史，在第一次之后聊天消息:{}", aiMessageQueryParam);
        Map<String, Object> result = new HashMap<>();
        messageService.updateMessage(aiMessageQueryParam.getReceiverId());
        PageResult<AIChatMessage> pageResult = messageService.historyMessagesAI(aiMessageQueryParam);
        result.put("message",pageResult);
        log.info("AIChatMessagesBySessionId/pull:获取历史，在第一次之后聊天消息成功:{}", pageResult);
        return result;
    }

    /**
     * 获取该用户的AI聊天历史信息
     * @param aiMessageQueryParam
     * @return
     */
    @PostMapping("/AIChatMessagesByUserId/pull")
    public Map<String, Object> AIHistoryMessage(@RequestBody AIMessageQueryParam aiMessageQueryParam){
        log.info("AIChatMessagesByUserId/pull:获取历史，在第一次之后聊天消息:{}", aiMessageQueryParam);
        Map<String, Object> result = new HashMap<>();
        messageService.updateMessage(aiMessageQueryParam.getReceiverId());
        PageResult<AIChatMessage> pageResult = messageService.historyMessageAI(aiMessageQueryParam);
        result.put("message",pageResult);
        log.info("AIChatMessagesByUserId/pull:获取历史，在第一次之后聊天消息成功:{}", pageResult);
        return result;
    }

    /**
     * 获取群聊历史，在第一次之后聊天消息
     * @param groupMessageQueryParam
     * @return
     */
    @PostMapping("/messagesGroup/pull")
    public Map<String, Object> historyMessagesGroup(@RequestBody GroupMessageQueryParam groupMessageQueryParam){
        log.info("messagesGroup/pull:获取历史，在第一次之后聊天消息:{}", groupMessageQueryParam);
        Map<String, Object> result = new HashMap<>();
        PageResult<ChatGroupMessage> pageResult = messageService.historyMessagesGroup(groupMessageQueryParam);
        result.put("message",pageResult);
        log.info("messagesGroup/pull:获取历史，在第一次之后聊天消息成功:{}", pageResult);
        return result;
    }
    /**
     * 获取联系人
     */
    @GetMapping("/contacts")
    public List<Contacts> getContacts(@RequestParam("id") Integer id){
        return messageService.getContacts(id);
    }

    /**
     * 第一次点击聊天获取聊天记录
     */
//    @PostMapping("/messages")
//    public Map<String, Object> getMessages(@RequestBody MessageQueryParam messageQueryParam){
//        String key="chat:"+messageQueryParam.getSenderId()+":"+messageQueryParam.getReceiverId();
//        log.info("message:获取历史，在第一次之后聊天消息:{}", messageQueryParam);
//        Map<String, Object> result = new HashMap<>();
//        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
//            List<Message> messages = messageService.getRedisMessages(key);
//            // 修复：不要强制转换，直接使用消息列表创建 PageResult
//            PageResult<Message> pageResult = new PageResult<>((long) messages.size(), messages);
//            result.put("message",pageResult);
//            return result;
//        }
//        PageResult<Message> pageResult = messageService.historyMessages(messageQueryParam);
//        messageService.setRedisMessages(key,pageResult.getRows());
//        result.put("message",pageResult);
//        log.info("message:获取历史，在第一次之后聊天消息成功:{}", pageResult);
//        return result;
//    }
}
