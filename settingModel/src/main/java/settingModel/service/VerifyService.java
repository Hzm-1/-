package settingModel.service;

import model.login.User;

public interface VerifyService {
    /**
     * 验证用户密码是否正确
     */
    boolean verifyPassword(Integer id,String password);
}
