package club.cybecraftman.leek;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BackTestApplication {

    // Tips: 启动后连接到Kafka，订阅Kafka的Topic消息开始进行回测
    // 需要记录回测的表，回测的结果，添加回测的锁。同时只能有一组回测

    public static void main(String[] args) {
        SpringApplication.run(BackTestApplication.class, args);
    }

}
