package message.service;



import model.AIChatModel.AIChatMessage;
import model.AIChatModel.AIMessageQueryParam;
import model.chatGroup.databaseClass.ChatGroupMessage;
import model.chatGroup.databaseClass.GroupMessageQueryParam;
import model.message.*;

import java.util.List;

public interface MessageService {
    List<Contacts> getContacts(Integer id);

    PageResult<Message> historyMessages(MessageQueryParam messageQueryParam);

    PageResult<AIChatMessage> historyMessagesAI(AIMessageQueryParam groupMessageQueryParam);

    public PageResult<AIChatMessage> historyMessageAI(AIMessageQueryParam aiMessageQueryParam);

    PageResult<ChatGroupMessage> historyMessagesGroup(GroupMessageQueryParam messageQueryParam);

    List<Message> getRedisMessages(String key);

    void setRedisMessages(String key,List<Message> messages);

    public void updateMessage(Integer id);
}
