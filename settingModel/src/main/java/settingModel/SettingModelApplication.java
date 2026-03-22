package settingModel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient //在每个微服务中都要添加该注解
@EnableFeignClients(basePackages = "settingModel.feign")
public class SettingModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(SettingModelApplication.class, args);
    }
}
