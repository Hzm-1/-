package chatGroupService.mapper;

import model.chatGroup.VO.ChatGroupVO;
import model.chatGroup.databaseClass.ChatGroup;
import model.chatGroup.databaseClass.ChatGroupUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface ChatGroupMapper {

    @Insert("insert into group_chat(group_name, group_avatar, creator_id, description, max_member, status, create_time, update_time) values(#{groupName}, #{groupAvatar}, #{creatorId}, #{description}, #{maxMember}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "groupId", keyColumn = "group_id")
    void createChatGroup(ChatGroupVO chatGroup);

    @Insert("insert into group_chat_member(group_id, user_id, role, status, join_time, create_time, update_time) values(#{group_id}, #{user_id}, #{role}, #{status}, #{join_time}, #{create_time}, #{update_time})")
    void addMember(ChatGroupUser chatGroupUser);

    void addMembers(List<ChatGroupUser> users);
}
