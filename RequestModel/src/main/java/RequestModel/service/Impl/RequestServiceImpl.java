package RequestModel.service.Impl;

import RequestModel.mapper.RequestMapper;
import RequestModel.service.RequestService;
import jakarta.annotation.Resource;
import model.addNew.Request;
import model.addNew.RequestReceiver;
import model.message.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Resource
    private RequestMapper requestMapper;

    public List<RequestReceiver> getRequest(String toUserEmail) {
        return requestMapper.getRequestByToUserEmail(toUserEmail);
    }

    @Override
    @Transactional
    public void acceptRequest(Person person1,Person person2) {
        person1.setStatus(1);
        person1.setCreatedAt(LocalDateTime.now());
        person1.setUpdatedAt(LocalDateTime.now());
        person1.setCategory("我的好友");

        person2.setStatus(1);
        person2.setCreatedAt(LocalDateTime.now());
        person2.setUpdatedAt(LocalDateTime.now());
        person2.setCategory("我的好友");
        requestMapper.acceptRequest(person1);
        requestMapper.acceptRequest(person2);

        requestMapper.updateRequestStatus(person2.getUserId(),person1.getUserId(),1);
    }
}
