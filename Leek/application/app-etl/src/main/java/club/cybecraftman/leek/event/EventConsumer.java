package club.cybecraftman.leek.event;

import club.cybecraftman.leek.common.event.LeekEvent;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventConsumer {

    @KafkaListener(topics = {LeekEvent.ON_BAR_RECEIVED.topic}, groupId = LeekEvent.ON_BAR_RECEIVED.group)
    public void onReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        String content = JSON.parseObject(bookConsumerRecord.value(), String.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), content);
    }

}
