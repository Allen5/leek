package club.cybercraftman.leek.event;

import club.cybercraftman.leek.common.event.creep.CreepEvent;
import club.cybercraftman.leek.common.event.LeekEvent;
import club.cybercraftman.leek.creeper.BaseCreeper;
import club.cybercraftman.leek.creeper.CreeperBuilder;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventConsumer {

    @Autowired
    private CreeperBuilder builder;

    @KafkaListener(topics = {LeekEvent.RUN_CREEP.topic}, groupId = LeekEvent.RUN_CREEP.group)
    public void onReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        CreepEvent event = JSON.parseObject(bookConsumerRecord.value(), CreepEvent.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), event);
        BaseCreeper creeper = builder.build(event);
        log.info("分配了[{}]开始执行爬取事件: {}", creeper.getClass().getName(), event);
        creeper.creep();
    }

}
