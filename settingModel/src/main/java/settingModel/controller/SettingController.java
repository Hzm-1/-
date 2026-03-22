package settingModel.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.springframework.web.bind.annotation.*;
import settingModel.service.SettingService;

@RestController
@RequestMapping("/setting")
@Slf4j
public class SettingController {
    @Resource
    private SettingService settingService;

    @PostMapping("/saveUser")
    public void saveUser(@RequestBody User user){
        settingService.saveUser(user);
    }

    @GetMapping("/savePassword")
    public void savePassword(@RequestParam("id") Integer id,@RequestParam("password") String password,@RequestParam("newPassword") String newPassword){
        log.info("用户修改密码:{},原密码：{},新密码：{}", id,password, newPassword);
        settingService.savePassword(id, password, newPassword);
    }
}
