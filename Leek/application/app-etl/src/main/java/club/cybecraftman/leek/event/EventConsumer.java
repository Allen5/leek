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

@Service
@Slf4j
public class EventConsumer {

    @Autowired
    private List<IBarService> barServices;

    @KafkaListener(topics = {LeekEvent.ON_BAR_RECEIVED.topic}, groupId = LeekEvent.ON_BAR_RECEIVED.group)
    public void onReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        BarEvent event = JSON.parseObject(bookConsumerRecord.value(), BarEvent.class);
        log.debug("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), event);
        try {
            dispatch(event);
        } catch (LeekException | LeekRuntimeException e) {
            log.error("分发消息失败: {}", event);
        }
    }

    /**
     * 分发行情数据
     * @param event
     */
    private void dispatch(BarEvent event) throws LeekException {
        IBarService service = null;
        for (IBarService bs : barServices) {
            if ( bs.isSupport(Market.parse(event.getMarketCode()), FinanceType.parse(event.getFinanceType()), BarType.parse(event.getBarType())) ) {
                service = bs;
                break;
            }
        }
        if (null == service) {
            log.error("不支持的行情处理类型[market: {}, financeType: {}, barType: {}]", event.getMarketCode(), event.getFinanceType(), event.getBarType());
            throw new LeekRuntimeException("不支持的行情处理类型");
        }
        service.handleBars(event.getItems());
    }

}
