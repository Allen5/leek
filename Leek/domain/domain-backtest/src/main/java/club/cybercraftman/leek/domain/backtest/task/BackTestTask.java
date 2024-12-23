package club.cybercraftman.leek.domain.backtest.task;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.constant.ValidStatus;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.BackTestRecordStatus;
import club.cybercraftman.leek.common.constant.trade.CommissionCategory;
import club.cybercraftman.leek.common.constant.trade.CommissionValueType;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.thread.AbstractTask;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.broker.Commission;
import club.cybercraftman.leek.core.eveluator.EvaluatorUtil;
import club.cybercraftman.leek.core.strategy.common.BaseStrategy;
import club.cybercraftman.leek.core.strategy.common.Signal;
import club.cybercraftman.leek.core.strategy.common.StrategyBuilder;
import club.cybercraftman.leek.domain.backtest.executor.BackTestRunningMode;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.ICommissionRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestRecordRepo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Slf4j
public abstract class BackTestTask extends AbstractTask {

    @Setter
    @Getter
    private BackTestRunningMode mode;

    @Setter
    private Market market;

    @Setter
    private FinanceType financeType;

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
            log.info("[回测:{}]交易日:{}, 交易标的: {}, 数据日期范围:[{}-{}], 当前金额: {}, 持仓:[], 订单:[]. 策略: {}",
                    this.record.getId(),
                    this.strategy.getCurrent(),
                    this.code,
                    dateRange.getStart(), dateRange.getEnd(),
                    this.strategy.getBroker().getCapital(),
                    this.strategy.getName());
            this.strategy.deal();                      // 处理前日挂单
            Signal signal = this.strategy.getSignal(); // 计算当日信号
            this.strategy.order(signal);               // 生成订单
        }
        // 获取当前剩余资金
        this.record.setFinalCapital(this.strategy.getBroker().getCapital());
    }

    @Override
    protected void onFail(String message) {
        this.record.setStatus(BackTestRecordStatus.FAIL.getStatus());
        this.record.setErrMessage( message.length() > BackTestRecord.MAX_MSG_LEN ? message.substring(0, BackTestRecord.MAX_MSG_LEN) : message);
        this.record.setUpdatedAt(new Date());
        this.record.setCost(this.record.getUpdatedAt().getTime() - this.record.getCreatedAt().getTime());
        IBackTestRecordRepo repo = SpringContextUtil.getBean(IBackTestRecordRepo.class);
        repo.save(record);
    }

    @Override
    protected void onSuccess() {
        // step4: 对策略结果进行评估计算
        EvaluatorUtil evaluator = SpringContextUtil.getBean(EvaluatorUtil.class);
        evaluator.evaluate(this.market, this.financeType, this.record, this.strategy.getCurrent());

        this.record.setStatus(BackTestRecordStatus.SUCCESS.getStatus());
        this.record.setUpdatedAt(new Date());
        this.record.setCost(this.record.getUpdatedAt().getTime() - this.record.getCreatedAt().getTime());
        IBackTestRecordRepo repo = SpringContextUtil.getBean(IBackTestRecordRepo.class);
        repo.save(record);
    }

    /**
     * 设置回测记录
     */
    private void initRecord(DateRange dateRange) {
        // 生成一条回测记录
        this.record = new BackTestRecord();
        record.setRunningMode(getMode().getMode());
        record.setStrategyId(this.strategy.getId());
        record.setStrategyClassName(this.strategy.getClass().getName());
        record.setStrategyName(this.strategy.getName());
        record.setCode(this.code);
        record.setStartDateTime(dateRange.getStart());
        record.setEndDateTime(dateRange.getEnd());
        record.setInitCapital(this.strategy.getBroker().getCapital());
        record.setFinalCapital(this.strategy.getBroker().getCapital());
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
        this.strategy.setMarket(market);
        this.strategy.setFinanceType(financeType);
        this.strategy.setCode(this.code);
        this.strategy.setParams(this.params);
        this.strategy.setBroker(Broker.builder()
                .capital(this.initCapital)
                .commissionMap(getCommissions())
                .build());
    }

    /**
     * 获取交易手续费
     * @return
     */
    private Map<CommissionCategory, Commission> getCommissions() {
        ICommissionRepo repo = SpringContextUtil.getBean(ICommissionRepo.class);
        List<club.cybercraftman.leek.repo.trade.model.Commission> commissions = repo.findAllByStatus(market.getCode(), financeType.getType(), ValidStatus.VALID.getStatus());
        if ( CollectionUtils.isEmpty(commissions) ) {
            return null;
        }
        return commissions.stream().map(c -> {
            Commission commission = new Commission();
            commission.setCategory(CommissionCategory.parse(c.getCategory()));
            commission.setValueType(CommissionValueType.parse(c.getType()));
            commission.setValue(c.getCommission());
            return commission;
        }).collect(Collectors.toMap(Commission::getCategory, c -> c));
    }

    protected abstract DateRange calcDateRange(final String code, final Integer startPercent, final Integer endPercent);

    /**
     * 计算日期范围内交易标的的回测数据数量
     * @param code 交易标的
     * @param start 日期范围开始时间
     * @param end   日期范围结束时间
     * @return  数量
     */
    protected abstract Integer countBars(final String code, final Date start, final Date end);

}
