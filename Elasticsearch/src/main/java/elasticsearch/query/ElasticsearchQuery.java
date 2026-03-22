package elasticsearch.query;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import model.login.User;
import org.apache.http.HttpHost;
import org.apache.tomcat.util.http.parser.Host;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ElasticsearchQuery {
    private static ElasticsearchClient client;
    private static final ElasticsearchTransport transport = null;

    @Value("${spring.Elasticsearch.host}")
    private String HOST;
    @Value("${spring.Elasticsearch.port}")
    private int PORT;

    // 使用@PostConstruct在依赖注入完成后初始化
    @PostConstruct //这个方法会在对象被创建并且所有依赖注入完成之后自动执行
    public void init() {
        initClient();
    }

    // 初始化客户端
    public void initClient() {
        // 创建支持Java 8日期时间类型的ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳的行为（可选）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 创建带有自定义ObjectMapper的JsonpMapper
        JsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        // 使用自定义mapper创建Elasticsearch客户端
        client = new ElasticsearchClient(
                new RestClientTransport(
                        RestClient.builder(
                                new HttpHost(HOST, PORT)
                        ).build(),
                        jsonpMapper
                )
        );
    }

    // 执行查询：搜索指定索引中符合条件的文档
    public List<User> searchUsers(String indexName, String keyword) throws IOException {
        List<User> users = new ArrayList<>();
        // 构建查询（匹配name字段中包含keyword的文档）
        SearchResponse<User> response = client.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                .match(m -> m
                                        .field("email")  // 要查询的字段
                                        .query(keyword)  // 查询关键词
                                )
                        )
                        .from(0)  // 分页起始位置
                        .size(10), // 每页数量
                User.class  // 结果映射的实体类
        );

        // 处理查询结果
        List<Hit<User>> hits = response.hits().hits();
        log.info("查询到 {} 条结果：", hits.size());

        for (Hit<User> hit : hits) {
            users.add(hit.source());
        }
        return users;
    }

    // 关闭客户端资源
    public void closeClient() throws IOException {
        transport.close(); // 关闭传输层会同时关闭restClient
    }
}
