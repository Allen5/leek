package club.cybecraftman.leek.creeper;

import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.domain.monitor.creep.CreepActionMonitor;
import club.cybecraftman.leek.infrastructure.mq.KafkaProducer;
import com.microsoft.playwright.Playwright;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class BaseCreeper<T> implements ICreeper {

    @Getter
    private Playwright playwright;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private CreepActionMonitor creepActionMonitor;

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
        try {
            this.doCreep();
            creepActionMonitor.success(logId);
        } catch (Exception e){
            log.error("[creeper: {}] 执行事件[{}] 失败", this.getClass().getName(), this.event, e);
            creepActionMonitor.fail(logId);
        }
        this.after();
        // 清理
        this.release();
    }

    protected abstract void before();
    protected abstract void after();
    protected abstract void doCreep();

    /**
     * 当前时间是否适合爬取该信息，默认为true
     * 对于行情有时点要求
     * @return
     */
    protected boolean isRightTime() {
        return true;
    }

    private void init() {
        // TODO: 初始化爬虫环境
    }

    private void release() {
        // TODO: 清理环境
    }

}
