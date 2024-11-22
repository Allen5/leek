package club.cybercraftman.leek.creeper;

import club.cybercraftman.leek.common.constant.finance.BarType;
import club.cybercraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybercraftman.leek.common.event.LeekEvent;
import club.cybercraftman.leek.common.event.etl.BarEvent;
import club.cybercraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.domain.monitor.creep.CreepActionMonitor;
import club.cybercraftman.leek.infrastructure.mq.KafkaProducer;
import club.cybercraftman.leek.repo.financedata.repository.ICalendarRepo;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class BaseCreeper implements ICreeper {

    // Tips: 后续改为配置
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

    @Override
    public void creep() {
        if ( !isRightTime() ) {
            return ;
        }
        Long logId = creepActionMonitor.init(this.getClass().getName(), this.event);
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
        options.setHeadless(true);
        try(Playwright playwright = Playwright.create();
            Browser browser = playwright.firefox().launch(options)) {
            Page page = browser.newPage();
            page.navigate(event.getSource());
            log.info("开始爬取地址: {}. event: {}. class: {}", event.getSource(), event, this.getClass().getName());
            this.doCreep(page);
            log.info("爬取地址: {}结束. event: {}. class: {}", event.getSource(), event, this.getClass().getName());
            creepActionMonitor.success(logId);
        } catch (Exception e){
            log.error("[creeper: {}] 执行事件[{}] 失败", this.getClass().getName(), this.event, e);
            creepActionMonitor.fail(logId, e.getMessage());
        }
    }

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
        return sdf.format(this.getCurrentTradeDate());
    }

    protected Date getCurrentTradeDate() {
        return calendarRepo.findCurrentTradeDate(getEvent().getMarketCode(), getEvent().getFinanceType(), new Date());
    }

    /**
     * 向消息队列推送收到的行情数据
     * @param barType
     * @param items
     */
    protected void publishBars(final BarType barType, final List<FutureBarEventData> items) {
        if (CollectionUtils.isEmpty(items) ){
            log.warn("本次推送数据为空，取消. barType: {}", barType);
            return ;
        }
        BarEvent pubEvent = new BarEvent();
        pubEvent.setBarType(barType.getType());
        pubEvent.setMarketCode(this.event.getMarketCode());
        pubEvent.setFinanceType(this.event.getFinanceType());
        pubEvent.setItems(JSONArray.parse(JSON.toJSONString(items)));
        getKafkaProducer().publish(LeekEvent.ON_BAR_RECEIVED.topic, pubEvent);
    }


}
