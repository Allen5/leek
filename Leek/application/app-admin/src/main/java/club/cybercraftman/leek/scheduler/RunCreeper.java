package club.cybercraftman.leek.scheduler;

import club.cybercraftman.leek.common.constant.creep.DataType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.event.creep.CreepEvent;
import club.cybercraftman.leek.common.event.LeekEvent;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.domain.meta.CreepConfigService;
import club.cybercraftman.leek.domain.monitor.creep.CreepActionMonitor;
import club.cybercraftman.leek.infrastructure.mq.KafkaProducer;
import club.cybercraftman.leek.repo.meta.model.CreeperConfig;
import club.cybercraftman.leek.repo.monitor.model.CreepLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RunCreeper {

    @Autowired
    private CreepConfigService creepConfigService;

    @Autowired
    private CreepActionMonitor creepActionMonitor;

    @Autowired
    private KafkaProducer producer;

    /**
     * 行情抓取
     * Market: CN
     * FinanceType: Future
     * DataType: Bar
     * 策略: 每隔30分钟抓取一次。
     * 判断逻辑：首先获取creeper list。 再获取monitor中的creeper_log，筛选未爬取成功的creeper
     * 发送run_creeper事件
     */
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void creepFutureBarOnCN() throws LeekException {
        log.info("=====> 开始爬取中国期货日交易行情信息");
        List<CreeperConfig> creepers = creepConfigService.findAvailableCreeps(Market.CN, FinanceType.FUTURE, DataType.BAR);
        log.info("=====> 共查询到[{}]个creeper配置信息", creepers.size());
        if ( CollectionUtils.isEmpty(creepers) ) {
            return;
        }

        // 查询今日有多少个creepers已经运行成功或正在运行中
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        List<CreepLog> logs = creepActionMonitor.findUnFailedLogs(Market.CN, FinanceType.FUTURE, DataType.BAR, start, end);
        Set<String> sourceNames = logs.stream().map(CreepLog::getSourceName).collect(Collectors.toSet());
        log.info("=====> 共查询到[{}]个creeper正在执行或已执行完成的执行记录", sourceNames.size());
        // 进行过滤
        creepers = creepers.stream().filter(creeper -> !sourceNames.contains(creeper.getSourceName()))
                .collect(Collectors.toList());
        log.info("=====> 根据sourceName过滤后，剩余[{}]个creeper配置待执行", creepers.size());
        // 生成event
        creepers.forEach(creeper -> {
            CreepEvent event = new CreepEvent();
            event.setMarketCode(Market.CN.getCode());
            event.setFinanceType(FinanceType.FUTURE.getType());
            event.setDataType(DataType.BAR.getType());
            event.setSourceName(creeper.getSourceName());
            event.setSource(creeper.getSource());
            producer.publish(LeekEvent.RUN_CREEP.topic, event);
        });
    }

}
