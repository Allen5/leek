package club.cybercraftman.leek;

import club.cybercraftman.leek.infrastructure.compute.job.JobSelector;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EtlApplication.class)
@Slf4j
public class EtlJobTest {

    private static final String SPARK_MASTER = "spark://0.0.0.0:7070";

    @Autowired
    JobSelector selector;

    @Test
    public void testFutureContractEtl() {
        this.run("F10000");
    }

    @Test
    public void testFutureBarEtl() {
        // 行情数据清洗
        this.run("F10000");
    }

    @Test
    public void testBackTestEtl() {
        // 回测数据清洗
        this.run("F10001");
    }

    private void run(final String functionId) {
        selector.findJob(functionId, SPARK_MASTER).action();
    }

}
