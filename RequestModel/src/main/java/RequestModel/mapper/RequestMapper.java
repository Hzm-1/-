package RequestModel.mapper;

import model.addNew.RequestReceiver;
import model.message.Person;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RequestMapper {

    /**
     * 根据toUserEmail查询所有请求
     */
    List<RequestReceiver> getRequestByToUserEmail(String toUserEmail);

    /**
     * 同意好友请求，插入person1对person2的记录
     */
    @Insert("insert into friend(user_id, friend_id, friend_nickname, status, created_at, updated_at, category) " +
            "values(#{userId},#{friendId},#{friendNickname},#{status},#{createdAt},#{updatedAt},#{category})")
    void acceptRequest(Person person);

    /**
     * 同意好友请求修改请求状态
     */
    @Update("update friend_requests set status=#{status} where from_user_id=#{fromUserId} and to_user_id=#{toUserId}")
    void updateRequestStatus(Integer fromUserId, Integer toUserId, Integer status);
}
