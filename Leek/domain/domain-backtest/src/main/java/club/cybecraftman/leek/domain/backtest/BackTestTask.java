package club.cybecraftman.leek.domain.backtest;

import club.cybecraftman.leek.common.thread.AbstractTask;
import club.cybecraftman.leek.core.strategy.IStrategy;
import club.cybecraftman.leek.repo.meta.repository.ICalendarRepo;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Setter
public class BackTestTask extends AbstractTask {

    /**
     * 回测周期的开始和结束时间
     */
    private Date startTime;

    private Date endTime;

    private IStrategy strategy;

    private BackTestParam param;

    @Autowired
    private ICalendarRepo calendarRepo;


    @Override
    protected void execute() {
        // 根据交易日里和回测周期获取回测的时间列表
        List<Calendar> calendars = loadTradeDays();
        // 对时间做for循环
        calendars.forEach(date -> {
            // TODO: 根据参数的周期范围加载数据
            // step2: 调用策略的onNextBar触发事件
            strategy.onNextBar();
        });
    }

    private List<Calendar> loadTradeDays() {
        return calendarRepo.findAllByMarketAndFinanceTypeDateRange(param.getMarketCode(),
                param.getFinanceType(),
                startTime,
                endTime);
    }


}
