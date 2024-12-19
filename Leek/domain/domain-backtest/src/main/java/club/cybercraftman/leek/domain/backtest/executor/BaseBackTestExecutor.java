package club.cybercraftman.leek.domain.backtest.executor;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.thread.CommonThreadPool;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.task.BackTestTask;
import club.cybercraftman.leek.repo.financedata.model.Calendar;
import club.cybercraftman.leek.repo.financedata.repository.ICalendarRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseBackTestExecutor implements IBackTestExecutor {

    @Setter
    @Getter
    private Market market;

    @Setter
    @Getter
    private FinanceType financeType;
    /**
     * 交易日期
     */
    @Getter
    private List<Calendar> calendars;

    @Autowired
    private ICalendarRepo calendarRepo;

    @Autowired
    private CommonThreadPool commonThreadPool;

    @Override
    public void execute(BackTestParam param) {
        // 初始化交易日期
        this.initCalendars(param.getDateRange());
        // 构建task
        List<BackTestTask> tasks = this.buildTasks(param);
        // 执行task
        commonThreadPool.execute(tasks);
    }

    protected void initCalendars(DateRange dateRange) {
        if ( ObjectUtils.isEmpty(dateRange) ) {
            this.calendars = calendarRepo.findAllByMarketAndFinanceType(this.market.getCode(), this.financeType.getType());
        } else {
            this.calendars = calendarRepo.findAllByMarketAndFinanceTypeAndDateRange(this.market.getCode(), this.financeType.getType(), dateRange.getStart(), dateRange.getEnd());
        }
    }

    /**
     * 获取回测的交易品种。股票为股票代码，期货为品种代码
     * @param minBars
     * @return
     */
    protected abstract List<String> loadCodes(final Integer minBars);

    /**
     * 创建对应的执行任务
     * @param code
     * @param startPercent
     * @param endPercent
     * @return
     */
    protected abstract BackTestTask createTask(final String code, final int startPercent, final int endPercent, final BackTestParam param);

    /**
     * 构建执行任务
     * @return
     */
    private List<BackTestTask> buildTasks(final BackTestParam param) {
        List<String> codes = this.loadCodes(param.getMinBars());
        // 确定数据范围. 规则: 开发环境 0~70%，测试：70%-80%，验证: 80%-100%
        int startPercent;
        int endPercent;
        if ( param.getMode().equals(BackTestRunningMode.DEV) ) {
            startPercent = 0;
            endPercent = 70;
        } else if ( param.getMode().equals(BackTestRunningMode.TEST) ) {
            startPercent = 70;
            endPercent = 80;
        } else {
            endPercent = 100;
            startPercent = 80;
        }
        return codes.stream().map(code -> this.createTask(code, startPercent, endPercent, param)).collect(Collectors.toList());
    }

}
