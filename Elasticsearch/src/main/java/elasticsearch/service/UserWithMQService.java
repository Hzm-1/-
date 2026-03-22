package elasticsearch.service;

import model.login.User;

import java.io.IOException;
import java.util.List;

public interface UserWithMQService {

    public List<User> searchUser(String email) throws IOException;
}
