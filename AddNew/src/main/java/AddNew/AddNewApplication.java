package AddNew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //在每个微服务中都要添加该注解
public class AddNewApplication {
    public static void main(String[] args) {
        SpringApplication.run(AddNewApplication.class, args);
    }
}
