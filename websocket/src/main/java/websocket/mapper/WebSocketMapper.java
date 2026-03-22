package websocket.mapper;

import model.chatGroup.databaseClass.ChatGroupMessage;
import model.message.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface WebSocketMapper {

    @Insert("insert into chat_message(receiver_id, sender_id, message_type, content, status, send_time, read_status) values(#{receiverId},#{senderId},#{messageType},#{content},#{status},#{sendTime},#{readStatus})")
    public void insertMessage(Message chatData);

    @Insert("insert into group_chat_record(group_id, sender_id, message_type, message_content, is_deleted, is_recalled, send_time, update_time) values(#{groupId}, #{senderId}, #{messageType}, #{messageContent}, #{isDeleted}, #{isRecalled}, #{sendTime}, #{updateTime})")
    void insertChatGroupMessage(ChatGroupMessage chatGroupMessage);
}
