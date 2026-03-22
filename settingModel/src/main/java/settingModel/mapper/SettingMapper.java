package settingModel.mapper;

import model.login.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SettingMapper {
    /**
     * 设置功能保存编辑
     */
    @Update("update user set nickname=#{nickname},phone=#{phone} where id=#{id}")
    void updateUser(User user);

    /**
     * 修改密码
     */
    @Update("update user set password=#{newPassword} where id=#{id}")
    void updatePassword(Integer id, String newPassword);

    /**
     * 验证密码是否正确
     */
    @Select("select * from user where id=#{id} and password=#{password}")
    User verifyPassword(Integer id,String password);
}
