package AddNew.service.Impl;

import AddNew.mapper.AddNewUserMapper;
import AddNew.service.AddNewUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.addNew.Request;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AddNewUserServiceImpl implements AddNewUserService {
    @Resource
    private AddNewUserMapper addNewUserMapper;

    @Override
    public void addNewUser(Request request) {
        request.setStatus(0);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setMessage(request.getMessage()==null?"":request.getMessage());
        addNewUserMapper.addNewUser(request);
    }

    @Override
    public boolean ifExistFriend(Request request){
        log.info("ifExistFriend:{}",request);
        return addNewUserMapper.ifExist(request) != null;
    }
}
