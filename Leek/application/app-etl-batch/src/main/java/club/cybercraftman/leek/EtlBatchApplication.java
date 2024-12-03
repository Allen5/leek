package club.cybercraftman.leek;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.job.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class EtlBatchApplication implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Autowired
    private List<BaseJob> jobs;

    public static void main(String[] args) {
        SpringApplication.run(EtlBatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String jobId = environment.getRequiredProperty("jobId");
        Optional<BaseJob> op = jobs.stream().filter(job -> job.getId().equalsIgnoreCase(jobId)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不存在ID为: " + jobId + " 的批处理任务");
        }
        op.get().etl(StreamExecutionEnvironment.getExecutionEnvironment());
        System.exit(0);
    }

}