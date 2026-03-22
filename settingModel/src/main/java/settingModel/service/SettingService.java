package settingModel.service;

import model.login.User;

public interface SettingService {

    void saveUser(User user);

    void savePassword(Integer id, String password, String newPassword);
}
