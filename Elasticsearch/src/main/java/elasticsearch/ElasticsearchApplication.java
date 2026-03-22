package elasticsearch;


import elasticsearch.feign.RabbitMQFeignClient;
import elasticsearch.synchronization.ElasticsearchSynchronization;
import feign.FeignException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient //在每个微服务中都要添加该注解
@EnableFeignClients(basePackages = "elasticsearch.feign")
@Slf4j
public class ElasticsearchApplication {
    @Resource
    private RabbitMQFeignClient rabbitMQFeignClient;
    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }
}
