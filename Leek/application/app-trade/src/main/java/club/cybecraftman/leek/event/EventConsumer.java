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

    @KafkaListener(topics = {LeekEvent.TRADE_SIGNAL.topic}, groupId = LeekEvent.TRADE_SIGNAL.group)
    public void onTradeSignal(ConsumerRecord<String, String> bookConsumerRecord) {
        String content = JSON.parseObject(bookConsumerRecord.value(), String.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), content);
    }

    @KafkaListener(topics = {LeekEvent.ON_ORDER.topic}, groupId = LeekEvent.ON_ORDER.group)
    public void onOrderReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        String content = JSON.parseObject(bookConsumerRecord.value(), String.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), content);
    }

    @KafkaListener(topics = {LeekEvent.ON_ORDER_CANCEL.topic}, groupId = LeekEvent.ON_ORDER_CANCEL.group)
    public void onOrderCancel(ConsumerRecord<String, String> bookConsumerRecord) {
        String content = JSON.parseObject(bookConsumerRecord.value(), String.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), content);
    }

}
