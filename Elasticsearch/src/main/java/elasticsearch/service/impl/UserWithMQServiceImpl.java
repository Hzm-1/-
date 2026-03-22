package elasticsearch.service.impl;

import elasticsearch.query.ElasticsearchQuery;
import elasticsearch.service.UserWithMQService;
import jakarta.annotation.Resource;
import model.login.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserWithMQServiceImpl implements UserWithMQService {

    @Resource
    private ElasticsearchQuery elasticsearchQuery;

    @Override
    public List<User> searchUser(String email) throws IOException {
        return elasticsearchQuery.searchUsers("user_index", email);
    }
}
