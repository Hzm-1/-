package chatGroupService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.chatGroup.VO.ChatGroupVO;

import java.util.List;

public interface ChatGroupService {

    /**
     * 创建群组
     * @param chatGroup
     */
    void createChatGroup(ChatGroupVO chatGroup) throws JsonProcessingException;

    /**
     * 添加到群聊的成员
     */
    void addMember(Integer groupId, Integer userId);

    /**
     * 批量添加群聊的成员
     */
    void addMembers(Integer groupId, List<Integer> userIds);
}
