package model.AIChatModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import model.message.MessageQueryParam;

@EqualsAndHashCode(callSuper = true)
@Data
public class AIMessageQueryParam extends MessageQueryParam {

    private String chatSessionId;


}
