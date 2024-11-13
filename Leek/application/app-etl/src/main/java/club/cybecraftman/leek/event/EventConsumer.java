package club.cybecraftman.leek.event;

import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.event.LeekEvent;
import club.cybecraftman.leek.common.event.etl.BarEvent;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.domain.financedata.IBarService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventConsumer {

    @Autowired
    private List<IBarService> barServices;

    @KafkaListener(topics = {LeekEvent.ON_BAR_RECEIVED.topic}, groupId = LeekEvent.ON_BAR_RECEIVED.group)
    public void onReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        BarEvent event = JSON.parseObject(bookConsumerRecord.value(), BarEvent.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), event);
    }

    /**
     * 分发行情数据
     * @param event
     */
    private void dispatch(BarEvent event) {
        Optional<IBarService> op = barServices.stream()
                .filter(bs -> {
                    try {
                        return bs.isSupport(Market.parse(event.getMarketCode()), FinanceType.parse(event.getFinanceType()), BarType.parse(event.getBarType()));
                    } catch (LeekException e) {
                        throw new LeekRuntimeException(e.getMessage());
                    }
                })
                .findAny();
        if (op.isEmpty()) {
            log.error("不支持的行情处理类型[market: {}, financeType: {}, barType: {}]", event.getMarketCode(), event.getFinanceType(), event.getBarType());
            throw new LeekRuntimeException("不支持的行情处理类型");
        }
        // Tips: 这边类型转换可能会有问题
        IBarService service = op.get();
        service.handleBars(event.getItems());
    }

}
