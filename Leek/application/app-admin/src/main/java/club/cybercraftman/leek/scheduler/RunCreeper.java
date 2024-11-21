package club.cybercraftman.leek.scheduler;

import club.cybercraftman.leek.repo.meta.repository.ICreeperConfigRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RunCreeper {

    @Autowired
    private ICreeperConfigRepo creeperConfigRepo;

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
    public void creepFutureBarOnCN() {
        log.info("=====> 开始爬取中国期货日交易行情信息");
    }

}
