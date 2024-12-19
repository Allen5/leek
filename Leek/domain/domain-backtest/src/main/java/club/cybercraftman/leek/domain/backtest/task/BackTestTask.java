package club.cybercraftman.leek.domain.backtest.task;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.constant.trade.BackTestRecordStatus;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.thread.AbstractTask;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.strategy.BaseStrategy;
import club.cybercraftman.leek.core.strategy.Signal;
import club.cybercraftman.leek.core.strategy.StrategyBuilder;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestRecordRepo;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Slf4j
public abstract class BackTestTask extends AbstractTask {

    /**
     * 策略实现类名
     */
    @Setter
    private String strategyClassName;

    /**
     * 交易日
     */
    @Setter
    private List<Date> tradeDays;

    /**
     * 交易策略
     */
    private BaseStrategy strategy;

    /**
     * 策略参数
     */
    @Setter
    private Map<String, Object> params;

    /**
     * 交易标的
     */
    @Setter
    private String code;

    @Setter
    private Integer startPercent;

    @Setter
    private Integer endPercent;

    @Setter
    private BigDecimal initCapital;

    private BackTestRecord record;


    @Override
    protected void execute() throws LeekException {
        // step1: 根据code, startPercent, endPercent确定其交易的真实起始截止日期
        DateRange dateRange = calcDateRange(this.code, this.startPercent, this.endPercent);
        // step2: 实例化策略
        initStrategy();
        // step3: 初始化backTestRecord
        initRecord(dateRange);
        this.strategy.setRecordId(this.record.getId());

        // step4: 逐日回测
        for (Date curDay : this.tradeDays ) {
            if ( curDay.before(dateRange.getStart()) ) {
                continue;
            }
            if ( curDay.after(dateRange.getEnd()) ) {
                break;
            }
            this.strategy.setCurrent(curDay);
            log.info("[回测:{}]交易日:{}, 交易标的: {}, 数据日期范围:[{}-{}], 持仓:[], 订单:[]. 策略: {}", this.record.getId(), this.strategy.getCurrent(), this.code, dateRange.getStart(), dateRange.getEnd(), this.strategy.getName());
            this.strategy.deal();                      // 处理前日挂单
            Signal signal = this.strategy.getSignal(); // 计算当日信号
            this.strategy.order(signal);               // 生成订单
        }
        // step4: 对策略结果进行评估
        this.evaluate();
    }

    /**
     * 设置回测记录
     */
    private void initRecord(DateRange dateRange) {
        // 生成一条回测记录
        this.record = new BackTestRecord();
        record.setStrategyId(this.strategy.getId());
        record.setStrategyClassName(this.strategy.getClass().getName());
        record.setStrategyName(this.strategy.getName());
        record.setCode(this.code);
        record.setStartDateTime(dateRange.getStart());
        record.setEndDateTime(dateRange.getEnd());
        record.setInitCapital(this.strategy.getBroker().getInitCapital());
        record.setFinalCapital(this.strategy.getBroker().getInitCapital());
        record.setParams(this.strategy.serializeParams());
        record.setStatus(BackTestRecordStatus.EXECUTING.getStatus());
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        IBackTestRecordRepo repo = SpringContextUtil.getBean(IBackTestRecordRepo.class);
        record.setBars(countBars(this.code, dateRange.getStart(), dateRange.getEnd()));
        record = repo.save(record);
    }

    private void initStrategy() {
        StrategyBuilder builder = SpringContextUtil.getBean(StrategyBuilder.class);
        this.strategy = builder.find(this.strategyClassName);
        this.strategy.setCode(this.code);
        this.strategy.setParams(this.params);
        this.strategy.setBroker(Broker.builder().initCapital(this.initCapital).build());
    }

    /**
     * 策略评价计算
     * TODO: 待梳理计算指标
     */
    @Transactional
    private void evaluate() {

        this.record.setStatus(BackTestRecordStatus.SUCCESS.getStatus());
        this.record.setUpdatedAt(new Date());
        this.record.setCost(this.record.getUpdatedAt().getTime() - this.record.getCreatedAt().getTime());

        IBackTestRecordRepo repo = SpringContextUtil.getBean(IBackTestRecordRepo.class);
        repo.save(record);
    }

    protected abstract DateRange calcDateRange(final String code, final Integer startPercent, final Integer endPercent);

    /**
     * 计算日期范围内交易标的的回测数据数量
     * @param code
     * @param start
     * @param end
     * @return
     */
    protected abstract Integer countBars(final String code, final Date start, final Date end);

}
