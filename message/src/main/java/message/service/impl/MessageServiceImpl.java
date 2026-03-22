package message.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import message.mapper.MessageMapper;
import message.service.MessageService;
import model.AIChatModel.AIChatMessage;
import model.AIChatModel.AIMessageQueryParam;
import model.chatGroup.VO.ChatGroupVO;
import model.chatGroup.databaseClass.ChatGroup;
import model.chatGroup.databaseClass.ChatGroupMessage;
import model.chatGroup.databaseClass.GroupMessageQueryParam;

import model.message.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 获取联系人
     */
    @Override
    public List<Contacts> getContacts(Integer id) {
        List<Contacts> contacts = new ArrayList<>();
        List<Person> users = messageMapper.getContacts(id);
        List<ChatGroup> groups = messageMapper.getChatGroups(id);
        for (Person user : users) {
            Contacts contact = new Contacts();

            user.setLastMessage(messageMapper.getLatestMessage(id,user.getFriendId())==null?new Message():messageMapper.getLatestMessage(id,user.getFriendId()));
            user.setUnreadCount(messageMapper.getUnreadMessageCount(id,user.getFriendId()));
            log.info("getContacts:获取联系人成功:{}", user);
            contact.setType(1);
            contact.setPerson(user);
            contacts.add(contact);
        }
        for (ChatGroup group : groups){
            //先进行类的转换
            ChatGroupVO vo = new ChatGroupVO();
            BeanUtils.copyProperties(group, vo);

            Contacts contact = new Contacts();
            contact.setType(2);
            contact.setChatGroup(vo);

            contact.getChatGroup().setLatestMessage(messageMapper.getLatestMessageForChatGroup(group.getGroupId())==null?new ChatGroupMessage():messageMapper.getLatestMessageForChatGroup(group.getGroupId()));
            contact.getChatGroup().setMembers(messageMapper.getChatGroupMembers(group.getGroupId()));

            contacts.add(contact);
        }

        return contacts;
    }

    /**
     * 获取聊天记录
     */
    @Override
    public PageResult<Message> historyMessages(MessageQueryParam messageQueryParam) {
        //设置分页参数（PageHelper）
        //参数分别为页码和每页记录数
        PageHelper.startPage(messageQueryParam.getPage(), messageQueryParam.getPageSize());

        //执行查询
        List<Message> list = messageMapper.historyMessages(messageQueryParam);

        //解析查询结果并封装
        Page<Message> p = (Page<Message>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    /**
     * 获取群组聊天记录
     * @param groupMessageQueryParam
     * @return
     */
    @Override
    public PageResult<ChatGroupMessage> historyMessagesGroup(GroupMessageQueryParam groupMessageQueryParam) {
        //设置分页参数（PageHelper）
        //参数分别为页码和每页记录数
        PageHelper.startPage(groupMessageQueryParam.getPage(), groupMessageQueryParam.getPageSize());

        //执行查询
        List<ChatGroupMessage> list = messageMapper.historyMessagesGroup(groupMessageQueryParam);
        log.info("群聊记录查询结果：{}",list);

        //解析查询结果并封装
        Page<ChatGroupMessage> p = (Page<ChatGroupMessage>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    /**
     * 获取AI聊天记录
     * @param aiMessageQueryParam
     * @return
     */
    @Override
    public PageResult<AIChatMessage> historyMessagesAI(AIMessageQueryParam aiMessageQueryParam) {
        //设置分页参数（PageHelper）
        //参数分别为页码和每页记录数
        PageHelper.startPage(aiMessageQueryParam.getPage(), aiMessageQueryParam.getPageSize());

        //执行查询
        List<AIChatMessage> list = messageMapper.historyMessagesAI(aiMessageQueryParam);
        log.info("AI聊天记录查询结果：{}",list);

        //解析查询结果并封装
        Page<AIChatMessage> p = (Page<AIChatMessage>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    /**
     * 获取AI聊天历史记录
     * @param aiMessageQueryParam
     * @return
     */
    @Override
    public PageResult<AIChatMessage> historyMessageAI(AIMessageQueryParam aiMessageQueryParam) {
        //设置分页参数（PageHelper）
        //参数分别为页码和每页记录数
        PageHelper.startPage(aiMessageQueryParam.getPage(), aiMessageQueryParam.getPageSize());

        //执行查询
        List<AIChatMessage> list = messageMapper.historyMessageAI(aiMessageQueryParam);
        log.info("AI聊天历史记录查询结果：{}",list);

        //解析查询结果并封装
        Page<AIChatMessage> p = (Page<AIChatMessage>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    /**
     * 获取redis存储的缓存聊天记录
     */
    @Override
    public List<Message> getRedisMessages(String key){
        List<Message> list = null;
        String json = redisTemplate.opsForValue().get(key);
        // 创建ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的默认行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            // 将JSON解析为User对象
            list = objectMapper.readValue(json, new TypeReference<List<Message>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 存储redis缓存聊天记录
     * id: chat:存储对象id:聊天对象id
     */
    @Override
    public void setRedisMessages(String key,List<Message> messages){
        // 创建ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的默认行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            // 将对象转换为JSON字符串
            String json = objectMapper.writeValueAsString(messages);
            redisTemplate.opsForValue().set(key,json,30, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改最新消息状态
     */
    public void updateMessage(Integer id){
        messageMapper.updateMessage(id);
    }
}
