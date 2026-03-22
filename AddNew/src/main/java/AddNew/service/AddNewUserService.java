package AddNew.service;

import model.addNew.Request;

public interface AddNewUserService {
    void addNewUser(Request request);

    public boolean ifExistFriend(Request request);
}
