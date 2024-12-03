package club.cybercraftman.leek;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class EtlStreamApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EtlStreamApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO: 初始化Flink执行环境
    }

}