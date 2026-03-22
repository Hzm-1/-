package AIChatModel.mapper;

import model.AIChatModel.AIChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AIChatMapper {

    @Insert("insert into t_ai_chat_message ( ai_id ,chat_session_id, user_id, message_role, message_content, message_type, send_time, message_status, error_msg, token_usage, ext_info) values " +
            "(#{aiId}, #{chatSessionId}, #{userId}, #{messageRole}, #{messageContent}, #{messageType}, #{sendTime}, #{messageStatus}, #{errorMsg}, #{tokenUsage}, #{extInfo})")
    void createChatRecord(AIChatMessage aiChatMessage);
}
