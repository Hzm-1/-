package settingModel.service.Impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import settingModel.feign.RabbitMQFeignClient;
import settingModel.mapper.SettingMapper;
import settingModel.service.SettingService;
import settingModel.service.VerifyService;

@Service
@Slf4j
public class SettingServiceImpl implements SettingService {
    @Resource
    private SettingMapper settingMapper;
    @Resource
    private VerifyService verifyService;
    @Resource
    private RabbitMQFeignClient rabbitMQFeignClient;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        try {
            log.info("用户信息保存成功:{}", user);
            settingMapper.updateUser(user);
            rabbitMQFeignClient.produceUpdateUserOfEs("ChatDemo.Elasticsearch", "routing.key.es.setting", user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePassword(Integer id,String password, String newPassword) {
        try{
            if (verifyService.verifyPassword(id,password)){
                settingMapper.updatePassword(id, newPassword);
            }else{
                throw new RuntimeException("密码错误");
            }
        }catch (Exception e){
            throw new RuntimeException("密码修改失败");
        }
    }
}
