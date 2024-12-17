package club.cybercraftman.leek;

import club.cybercraftman.leek.infrastructure.compute.job.AbstractEtlJob;
import club.cybercraftman.leek.infrastructure.compute.job.JobSelector;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EtlApplication.class)
@Slf4j
public class EtlJobTest {

    @Autowired
    JobSelector selector;

    @Test
    public void testFutureContractEtl() {
        String functionId = "F10000";
        String masterUrl = "spark://0.0.0.0:7070";
        AbstractEtlJob job = selector.findJob(functionId, masterUrl);
        job.action();
    }

}
