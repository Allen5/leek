package club.cybercraftman.leek.domain.backtest;

import club.cybercraftman.leek.common.thread.AbstractTask;
import club.cybercraftman.leek.core.strategy.IStrategy;
import club.cybercraftman.leek.core.strategy.dto.Bar;
import club.cybercraftman.leek.repo.financedata.model.Calendar;
import club.cybercraftman.leek.repo.financedata.repository.ICalendarRepo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Setter
@Slf4j
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
            // TODO: 修整bar参数
            log.debug("[回测][日期: {}][策略: {}]开始调用onNextBar", date, strategy.getClass().getName());
            strategy.onNextBar(new Bar());
            log.debug("[回测][日期: {}][策略: {}]调用onNextBar结束", date, strategy.getClass().getName());
        });
    }

    private List<Calendar> loadTradeDays() {
        return calendarRepo.findAllByMarketAndFinanceTypeAndDateRange(param.getMarketCode(),
                param.getFinanceType(),
                startTime,
                endTime);
    }


}
