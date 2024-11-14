package club.cybecraftman.leek.creeper;

import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.domain.monitor.creep.CreepActionMonitor;
import club.cybecraftman.leek.infrastructure.mq.KafkaProducer;
import club.cybecraftman.leek.repo.financedata.repository.ICalendarRepo;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public abstract class BaseCreeper<T> implements ICreeper {

    public static final String DOWNLOAD_FILE_ROOT_DIR = "download";

    @Getter
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private CreepActionMonitor creepActionMonitor;

    @Autowired
    @Getter
    private ICalendarRepo calendarRepo;

    @Getter
    @Setter
    private CreepEvent event;

    @Getter
    @Setter
    private T data;

    @Override
    public void creep() {
        if ( !isRightTime() ) {
            return ;
        }
        // 初始化
        this.before();
        Long logId = creepActionMonitor.init(this.getClass().getName(), this.event);
        try(Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch()) {
            Page page = browser.newPage();
            page.navigate(event.getSource());
            log.info("开始爬取地址: {}. event: {}. class: {}", event.getSource(), event, this.getClass().getName());
            this.doCreep(page);
            log.info("爬取地址: {}结束. event: {}. class: {}", event.getSource(), event, this.getClass().getName());
            creepActionMonitor.success(logId);
        } catch (Exception e){
            log.error("[creeper: {}] 执行事件[{}] 失败", this.getClass().getName(), this.event, e);
            creepActionMonitor.fail(logId);
        }
        this.after();
    }

    protected abstract void before();
    protected abstract void after();
    protected abstract void doCreep(final Page page) throws LeekException;

    /**
     * 当前时间是否适合爬取该信息，默认为true
     * 对于行情有时点要求
     * @return
     */
    protected boolean isRightTime() {
        return true;
    }

    /**
     * 获取当前交易日
     * @param sdf
     * @return
     */
    protected String getCurrentTradeDate(final SimpleDateFormat sdf) {
        Date tradeDate = calendarRepo.findCurrentTradeDate(getEvent().getMarketCode(), getEvent().getFinanceType(), new Date());
        return sdf.format(tradeDate);
    }


}
