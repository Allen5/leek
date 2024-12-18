package club.cybercraftman.leek.domain.backtest.task.future;

import club.cybercraftman.leek.core.strategy.IStrategy;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.task.BackTestTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
public class FutureBackTestTask extends BackTestTask {

    public FutureBackTestTask(List<Date> tradeDays, IStrategy strategy, String code, Integer startPercent, Integer endPercent) {
        super(tradeDays, strategy, code, startPercent, endPercent);
    }

    @Override
    protected BackTestParam.DateRange calcDateRange(String code, Integer startPercent, Integer endPercent) {
        return null;
    }

}
