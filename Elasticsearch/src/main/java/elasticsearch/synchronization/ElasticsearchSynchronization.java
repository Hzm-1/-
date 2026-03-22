package elasticsearch.synchronization;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import elasticsearch.feign.RabbitMQFeignClient;
import lombok.extern.slf4j.Slf4j;
import model.chatGroup.databaseClass.ChatGroup;
import model.login.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class ElasticsearchSynchronization {
    ElasticsearchClient elasticsearchClient;

    public ElasticsearchSynchronization(ElasticsearchClient elasticsearchClient,
                                        RabbitMQFeignClient rabbitMQFeignClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    /**
     * 定时任务，每隔1分钟从RabbitMQ同步一次数据
     *
     * 方法返回值必须为void
     * 方法不能有参数
     * 方法一般应为非静态方法
     *
     * 方法所在类需被 Spring 管理
     *
     * 需要在配置类上添加@EnableScheduling注解，以开启 Spring 的定时任务调度功能。
     */
//    @Scheduled(fixedDelay = 60000)

    /**
     * ES记录用户信息
     * @param user
     * @throws IOException
     */
    public void synchronizeUser(User user) throws IOException {
        log.info("synchronizeUser消费数据数：{}",user);
        // 批量插入用户数据到Elasticsearch
        elasticsearchClient.index(i -> i
                .index("user_index")
                .id(String.valueOf(user.getId()))
                .document(user)
        );
    }

    /**
     * ES记录群聊信息
     * @param group
     * @throws IOException
     */
    public void synchronizeGroup(ChatGroup group) throws IOException {
        log.info("synchronizeGroup消费数据数：{}",group);
        // 批量插入用户数据到Elasticsearch
        elasticsearchClient.index(i -> i
                .index("group_chat")
                .id(String.valueOf(group.getGroupId()))
                .document(group)
        );
    }

    public void updateUser(User user) throws IOException {
        // 待更新的字段（仅修改age和address，其他字段不变）
        Map<String, Object> updateDoc = Map.of(
                "nickname", user.getNickname()
        );

        // 执行增量更新
        elasticsearchClient.update(u -> u
                        .index("user_index")
                        .id(String.valueOf(user.getId())) // 指定文档ID
                        .doc(updateDoc), // 设置要更新的字段
                Map.class // 响应结果的类型
        );
    }
}
