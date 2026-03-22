package RequestModel.service;


import model.addNew.RequestReceiver;
import model.message.Person;

import java.util.List;

public interface RequestService {
    //获取该用户的请求添加好友列表
    public List<RequestReceiver> getRequest(String toUserEmail);

    //接受
    public void acceptRequest(Person person1,Person person2);
}
