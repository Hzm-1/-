package RequestModel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient //在每个微服务中都要添加该注解
@SpringBootApplication
public class RequestModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestModelApplication.class, args);
    }
}
