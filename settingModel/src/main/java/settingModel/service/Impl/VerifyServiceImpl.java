package settingModel.service.Impl;

import jakarta.annotation.Resource;
import model.login.User;
import org.springframework.stereotype.Service;
import settingModel.mapper.SettingMapper;
import settingModel.service.SettingService;
import settingModel.service.VerifyService;

@Service
public class VerifyServiceImpl implements VerifyService {

    @Resource
    private SettingMapper settingMapper;

    @Override
    public boolean verifyPassword(Integer id, String password) {
        return settingMapper.verifyPassword(id,password)!=null;
    }
}
