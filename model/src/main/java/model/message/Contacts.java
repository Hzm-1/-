package model.message;

import lombok.Data;
import model.chatGroup.VO.ChatGroupVO;

/**
 * 用于整合所有可被展示出的联系人列表消息，现包括：
 *      好友，
 *      群聊
 */
@Data
public class Contacts {
    // 类型（1-好友，2-群聊），用于前端判断
    private Integer type;

    // 联系人
    private Person person;

    // 群聊
    private ChatGroupVO chatGroup;
}
