package model.chatGroup.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import model.chatGroup.databaseClass.ChatGroup;
import model.chatGroup.databaseClass.ChatGroupMessage;
import model.chatGroup.databaseClass.ChatGroupUser;


import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatGroupVO extends ChatGroup {

    // 群成员
    private List<ChatGroupUserVO> members;

    // 群最新消息
    private ChatGroupMessage latestMessage;
}
