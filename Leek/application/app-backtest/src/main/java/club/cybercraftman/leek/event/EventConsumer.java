package club.cybercraftman.leek.event;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.event.LeekEvent;
import club.cybercraftman.leek.common.event.backtest.RunBackTestEvent;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.strategy.StrategyBuilder;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.executor.BackTestExecutorBuilder;
import club.cybercraftman.leek.domain.backtest.executor.BackTestRunningMode;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class EventConsumer {

    @Autowired
    private BackTestExecutorBuilder builder;

    @Autowired
    private StrategyBuilder strategyBuilder;

    @KafkaListener(topics = {LeekEvent.RUN_BACKTEST.topic}, groupId = LeekEvent.RUN_BACKTEST.group)
    public void onReceive(ConsumerRecord<String, String> bookConsumerRecord) {
        RunBackTestEvent content = JSON.parseObject(bookConsumerRecord.value(), RunBackTestEvent.class);
        log.info("收到回测执行事件消费topic:{} partition:{}的消息 -> {}", bookConsumerRecord.topic(), bookConsumerRecord.partition(), content);
        try {
            this.dispatch(content);
        } catch (LeekException e) {
            log.error("回测执行异常: ", e);
            throw new LeekRuntimeException(e.getMessage());
        }
    }

    private void dispatch(RunBackTestEvent event) throws LeekException {
        BackTestParam params = BackTestParam.builder()
                .mode(BackTestRunningMode.parse(event.getRunningMode()))
                .minBars(event.getMinBars())
                .capital(new BigDecimal(event.getCapital()))
                .strategyClassName(event.getStrategyClassName())
                .strategyParams(event.getStrategyParams())
                .dateRange(new DateRange(event.getStartDate(), event.getEndDate()))
                .build();
        builder.find(Market.parse(event.getMarketCode()), FinanceType.parse(event.getFinanceType()))
                .execute(params);
    }

}
