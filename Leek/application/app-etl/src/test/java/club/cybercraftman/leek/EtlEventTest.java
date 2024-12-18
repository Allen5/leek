package club.cybercraftman.leek;

import club.cybercraftman.leek.common.event.LeekEvent;
import club.cybercraftman.leek.common.event.etl.EtlEvent;
import club.cybercraftman.leek.infrastructure.mq.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EtlApplication.class)
@Slf4j
public class EtlEventTest {

    private static final String SPARK_MASTER = "spark://localhost:7070";


    @Autowired
    private KafkaProducer producer;

    @Test
    public void testPublishEvent() {
        EtlEvent event = new EtlEvent();
        event.setFunctionId("F10000");
        event.setMasterUrl(SPARK_MASTER);
        producer.publish(LeekEvent.ON_ETL_TRIGGERED.topic, event);
    }

}
