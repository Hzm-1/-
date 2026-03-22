package message.mapper;

import model.AIChatModel.AIChatMessage;
import model.AIChatModel.AIMessageQueryParam;
import model.chatGroup.VO.ChatGroupUserVO;
import model.chatGroup.databaseClass.ChatGroup;
import model.chatGroup.databaseClass.ChatGroupMessage;
import model.chatGroup.databaseClass.GroupMessageQueryParam;
import model.message.Message;
import model.message.MessageQueryParam;
import model.message.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 根据id获取对应的好友信息
     */
    @Select("select friend_id,friend_nickname,status,created_at,updated_at,category from friend where user_id=#{id}")
    public List<Person> getContacts(Integer id);

    /**
     * 获取聊天记录
     */
    @Select("SELECT id, receiver_id, sender_id, message_type, content, file_name, file_size, file_path, status, send_time, read_status " +
            "FROM chat_message WHERE (receiver_id = #{receiverId} and sender_id = #{senderId}) or (receiver_id = #{senderId} and sender_id = #{receiverId}) " +
            "ORDER BY send_time DESC")
    public List<Message> historyMessages(MessageQueryParam messageQueryParam);

    /**
     * 获取群聊记录
     */
    @Select("SELECT record_id, group_id, sender_id, message_type, message_content, is_deleted, is_recalled, send_time, update_time " +
            "FROM group_chat_record WHERE group_id = #{groupId} and is_recalled = 0 and is_deleted = 0 " +
            "ORDER BY send_time DESC")
    public List<ChatGroupMessage> historyMessagesGroup(GroupMessageQueryParam messageQueryParam);
    /**
     * 获取到指定用户的最新消息
     */
    @Select("SELECT id, receiver_id, sender_id, message_type, content, file_name, file_size, file_path, status, send_time, read_status " +
            "FROM chat_message WHERE (receiver_id = #{userId} and sender_id = #{friendId}) or (receiver_id = #{friendId} and sender_id = #{userId}) " +
            "ORDER BY send_time DESC LIMIT 1")
    public Message getLatestMessage(Integer userId ,Integer friendId);

    /**
     * 获取未读消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE receiver_id = #{userId} and sender_id = #{friendId} AND read_status = 0")
    public int getUnreadMessageCount(Integer userId, Integer friendId);

    /**
     * 将未读的消息标记位已读
     */
    @Update("update chat_message set read_status = 1 where receiver_id = #{id} AND read_status = 0")
    public void updateMessage(Integer id);

    /**
     * 获取群聊
     */
    @Select("SELECT gc.* " +
            "FROM group_chat gc " +
            "INNER JOIN group_chat_member gcm ON gc.group_id = gcm.group_id " +
            "WHERE gcm.user_id = #{userId} " +
            "  AND gcm.status = 1 " + // 仅查询用户“正常在群”的群聊（排除已退出/被踢的）
            "  AND gc.status = 1")    // 仅查询群聊“正常”的（排除解散/封禁的）
    List<ChatGroup> getChatGroups(Integer userId);

    /**
     * 获取群聊的最新消息
     * @param groupId
     * @return
     */
    @Select("SELECT record_id, group_id, sender_id, message_type, message_content, is_deleted, is_recalled, send_time, update_time " +
            "FROM group_chat_record WHERE group_id = #{groupId}  " +
            "ORDER BY send_time DESC LIMIT 1")
    ChatGroupMessage getLatestMessageForChatGroup(Integer groupId);

    /**
     * 获取群聊的成员
     */
    @Select("SELECT id, group_id, user_id, role, status, join_time, quit_time, create_time, update_time " +
            "FROM group_chat_member WHERE group_id = #{groupId}")
    List<ChatGroupUserVO> getChatGroupMembers(Integer groupId);

    /**
     * 获取AI同轮聊天记录
     * @param aiMessageQueryParam
     * @return
     */
    @Select("SELECT id, ai_id, chat_session_id, user_id, message_role, message_content, message_type, send_time, message_status, error_msg, token_usage, ext_info " +
            "FROM t_ai_chat_message WHERE user_id = #{senderId} and chat_session_id = #{chatSessionId} " +
            "ORDER BY send_time DESC")
    List<AIChatMessage> historyMessagesAI(AIMessageQueryParam aiMessageQueryParam);

    /**
     * 获取senderId的AI一定数量的使用记录
     * @param aiMessageQueryParam
     * @return
     */
    @Select("""
            SELECT t.id, t.ai_id, t.chat_session_id, t.user_id, t.message_role, t.message_content, t.message_type, t.send_time, t.message_status, t.error_msg, t.token_usage, t.ext_info
                         FROM t_ai_chat_message t
                         INNER JOIN (
                             SELECT
                                 chat_session_id,
                                 MIN(send_time) AS min_send_time
                             FROM t_ai_chat_message
                             WHERE user_id = #{senderId}
                             GROUP BY chat_session_id
                         ) temp ON t.chat_session_id = temp.chat_session_id AND t.send_time = temp.min_send_time
                         WHERE t.user_id = #{senderId}
                         ORDER BY t.chat_session_id, t.send_time""")
    List<AIChatMessage> historyMessageAI(AIMessageQueryParam aiMessageQueryParam);
}

