package club.cybercraftman.leek.domain.backtest.task;

import club.cybercraftman.leek.common.thread.AbstractTask;
import club.cybercraftman.leek.core.strategy.IStrategy;
import club.cybercraftman.leek.core.strategy.Signal;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Slf4j
public abstract class BackTestTask extends AbstractTask {

    /**
     * 交易日
     */
    private List<Date> tradeDays;

    /**
     * 交易策略
     */
    private IStrategy strategy;

    /**
     * 交易标的
     */
    private String code;

    private Integer startPercent;

    private Integer endPercent;

    public BackTestTask(final List<Date> tradeDays, final IStrategy strategy, final String code, final Integer startPercent, final Integer endPercent) {
        this.tradeDays = tradeDays;
        this.strategy = strategy;
        this.code = code;
        this.startPercent = startPercent;
        this.endPercent = endPercent;
    }

    private Long recordId;

    @Override
    protected void execute() {
        // step1: 初始化backTestRecord
        initRecord();
        // step2: 根据code, startPercent, endPercent确定其交易的真实起始截止日期
        BackTestParam.DateRange dateRange = calcDateRange(this.code, this.startPercent, this.endPercent);
        for (Date curDay : this.tradeDays ) {
            if ( curDay.before(dateRange.getStart()) ) {
                continue;
            }
            if ( curDay.after(dateRange.getEnd()) ) {
                break;
            }
            log.info("[回测:{}]交易日:{}, 交易标的: {}, 数据日期范围:[{}-{}], 持仓:[], 订单:[]. 策略: {}", this.recordId, curDay, this.code, dateRange.getStart(), dateRange.getEnd(), this.strategy.getName());
            // this.strategy.onOrder(curDay); // TODO: 处理上一日的订单
            Signal signal = this.strategy.next(curDay);
            // TODO: 执行交易
        }
        // step3: 对策略结果进行评估
        this.evaluate();
    }

    /**
     * 设置回测记录
     */
    private void initRecord() {

    }

    private void evaluate() {

    }

    protected abstract BackTestParam.DateRange calcDateRange(final String code, final Integer startPercent, final Integer endPercent);


}
