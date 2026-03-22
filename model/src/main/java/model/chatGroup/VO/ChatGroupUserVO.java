package model.chatGroup.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import model.chatGroup.databaseClass.ChatGroupUser;
import model.message.Person;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatGroupUserVO extends ChatGroupUser {
    //群成员详细详细
    private Person member;
}
