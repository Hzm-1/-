package model.chatGroup.databaseClass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import model.message.MessageQueryParam;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupMessageQueryParam extends MessageQueryParam {
    private Integer groupId;
}
