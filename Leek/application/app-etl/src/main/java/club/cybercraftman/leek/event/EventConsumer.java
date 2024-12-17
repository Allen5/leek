package club.cybercraftman.leek.event;

import club.cybercraftman.leek.common.constant.finance.BarType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.event.LeekEvent;
import club.cybercraftman.leek.common.event.etl.BarEvent;
import club.cybercraftman.leek.common.event.etl.EtlEvent;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.domain.financedata.IBarService;
import club.cybercraftman.leek.infrastructure.compute.job.AbstractEtlJob;
import club.cybercraftman.leek.infrastructure.compute.job.JobSelector;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EventConsumer {

    @Autowired
    private List<IBarService> barServices;

    @Autowired
    private JobSelector jobSelector;

    @KafkaListener(topics = {LeekEvent.ON_BAR_RECEIVED.topic}, groupId = LeekEvent.ON_BAR_RECEIVED.group)
    public void onReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        BarEvent event = JSON.parseObject(bookConsumerRecord.value(), BarEvent.class);
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), event);
        try {
            dispatch(event);
        } catch (LeekException | LeekRuntimeException e) {
            log.error("分发消息失败: {}", event);
        }
    }

    /**
     * ETL事件监听
     * @param bookConsumerRecord
     */
    @KafkaListener(topics = {LeekEvent.ON_ETL_TRIGGERED.topic}, groupId = LeekEvent.ON_ETL_TRIGGERED.group)
    public void onReceiveEtl(ConsumerRecord<String, String> bookConsumerRecord) {
        EtlEvent event = JSON.parseObject(bookConsumerRecord.value(), EtlEvent.class);
        log.info("收到ETL事件。topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), event);
        CompletableFuture.runAsync(() -> jobSelector.findJob(event.getFunctionId(), event.getMasterUrl()).action());
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
