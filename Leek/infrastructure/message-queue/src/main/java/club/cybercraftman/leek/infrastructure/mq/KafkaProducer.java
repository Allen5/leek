package club.cybercraftman.leek.infrastructure.mq;

import club.cybercraftman.leek.common.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaProducer implements IProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publish(String topicName, BaseEvent data) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, data);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                log.info("生产者成功发送消息到{}-> {}", topicName, sendResult.getProducerRecord().value().toString());
            }
            @Override
            public void onFailure(Throwable throwable) {
                log.error("生产者发送消息：{} 失败，原因：{}", data.toString(), throwable.getMessage());
            }
        });
    }

}
