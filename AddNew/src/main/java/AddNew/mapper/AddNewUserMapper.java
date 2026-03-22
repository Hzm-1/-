package AddNew.mapper;

import model.addNew.Request;
import model.message.Person;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddNewUserMapper {

    @Insert("insert into friend_requests (from_user_email,from_user_id, to_user_email,to_user_id,remark,friend_group, status, message, created_at, updated_at) values (#{fromUserEmail},#{fromUserId},#{toUserEmail},#{toUserId},#{remark},#{friendGroup},#{status},#{message},#{createdAt},#{updatedAt})")
    void addNewUser(Request request);

    @Select("select * from friend where user_id=#{fromUserId} and friend_id=#{toUserId}")
    Person ifExist(Request request);
}
