package chatGroupService.service.Impl;

import chatGroupService.feign.RabbitMQFeignClient;
import chatGroupService.mapper.ChatGroupMapper;
import chatGroupService.service.ChatGroupService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.chatGroup.VO.ChatGroupUserVO;
import model.chatGroup.VO.ChatGroupVO;
import model.chatGroup.databaseClass.ChatGroup;
import model.chatGroup.databaseClass.ChatGroupUser;
import model.message.Person;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ChatGroupServiceImpl implements ChatGroupService {
    @Resource
    private ChatGroupMapper chatGroupMapper;
    @Resource
    private RabbitMQFeignClient rabbitMQFeignClient;
    // 创建ObjectMapper对象
    static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的默认行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Transactional
    @Override
    @GlobalTransactional
    public void createChatGroup(ChatGroupVO chatGroup) throws JsonProcessingException {
        ChatGroup chatGroupDB = new ChatGroup();
        Person person = new Person();
        // 群主
        log.info("正在创建群主信息：{}", chatGroup.getCreatorId());
        ChatGroupUserVO chatGroupUserVO = new ChatGroupUserVO();
        chatGroupUserVO.setGroupId(chatGroup.getGroupId());
        chatGroupUserVO.setUserId(chatGroup.getCreatorId());
        chatGroupUserVO.setRole(1);
        chatGroupUserVO.setStatus(1);
        chatGroupUserVO.setJoinTime(LocalDateTime.now());
        chatGroupUserVO.setCreatedTime(LocalDateTime.now());
        chatGroupUserVO.setUpdatedTime(LocalDateTime.now());

        person.setId(chatGroupUserVO.getUserId());
        chatGroupUserVO.setMember(person);

        chatGroup.getMembers().add(chatGroupUserVO);

        log.info("正在创建群组信息：{}", chatGroup);
        chatGroup.setStatus(1);
        chatGroup.setMaxMember(200);
        chatGroup.setCreateTime(LocalDateTime.now());
        chatGroup.setUpdateTime(LocalDateTime.now());
        List<Integer> userIds =  chatGroup.getMembers().stream().map(member ->
                member.getMember().getId()
        ).toList();

        BeanUtils.copyProperties(chatGroup,chatGroupDB);

        log.info("正在创建群聊");
        chatGroupMapper.createChatGroup(chatGroup);
        addMembers(chatGroup.getGroupId(), userIds);
        rabbitMQFeignClient.sendEsTask("ChatDemo.Elasticsearch","queue.elasticsearch.group",objectMapper.writeValueAsString(chatGroupDB));
    }

    @Override
    public void addMember(Integer groupId, Integer userId) {
        ChatGroupUser chatGroupUser = new ChatGroupUser();
        chatGroupUser.setGroupId(groupId);
        chatGroupUser.setUserId(userId);
        chatGroupUser.setRole(1);
        chatGroupUser.setStatus(1);
        chatGroupUser.setJoinTime(LocalDateTime.now());
        chatGroupUser.setCreatedTime(LocalDateTime.now());
        chatGroupUser.setUpdatedTime(LocalDateTime.now());
        chatGroupMapper.addMember(chatGroupUser);
    }

    @Override
    public void addMembers(Integer groupId, List<Integer> userIds) {
        List<ChatGroupUser> chatGroupUsers =  userIds.stream().map(userId -> {
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setGroupId(groupId);
            chatGroupUser.setUserId(userId);
            chatGroupUser.setRole(3);
            chatGroupUser.setStatus(1);
            chatGroupUser.setJoinTime(LocalDateTime.now());
            chatGroupUser.setCreatedTime(LocalDateTime.now());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            return chatGroupUser;
        }).toList();
        chatGroupMapper.addMembers(chatGroupUsers);
    }
}
