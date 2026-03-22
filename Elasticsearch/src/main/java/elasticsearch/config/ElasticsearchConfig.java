package elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    // 宿主机 IP（Docker 所在 Linux 机器的 IP）
    @Value("${spring.Elasticsearch.host}")
    private String ES_HOST;
    // 映射的 HTTP 端口（与 Docker 启动时的 -p 9200:9200 对应）
    @Value("${spring.Elasticsearch.port}")
    private int ES_PORT;
    // 协议：HTTP（因已关闭 HTTPS）
    @Value("${spring.Elasticsearch.protocol}")
    private String ES_SCHEME;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 1. 构建 RestClient（连接 Docker 暴露的 9200 端口）
        RestClient restClient = RestClient.builder(
                new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)
        ).build();

        // 2. 配置 Jackson 支持 LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 3. 创建传输层和客户端
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper(objectMapper)
        );

        return new ElasticsearchClient(transport);
    }
}