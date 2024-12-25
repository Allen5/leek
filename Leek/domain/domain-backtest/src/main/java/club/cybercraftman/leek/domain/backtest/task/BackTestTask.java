package club.cybercraftman.leek.domain.backtest.task;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.BackTestRecordStatus;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.thread.AbstractTask;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.eveluator.EvaluatorUtil;
import club.cybercraftman.leek.core.service.BackTestDailyStatService;
import club.cybercraftman.leek.core.service.BackTestOrderService;
import club.cybercraftman.leek.core.service.BackTestPositionService;
import club.cybercraftman.leek.core.strategy.common.BaseStrategy;
import club.cybercraftman.leek.core.strategy.common.Signal;
import club.cybercraftman.leek.core.strategy.common.StrategyBuilder;
import club.cybercraftman.leek.domain.backtest.executor.BackTestRunningMode;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestRecordRepo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        log.info("[回测Id: {} -- 线程ID: {} -- 交易标的: {}] == 开始逐bar执行",
                this.record.getId(),
                Thread.currentThread().getId(),
                this.code);
        // step4: 逐日回测
        BackTestOrderService orderService = SpringContextUtil.getBean(BackTestOrderService.class);
        BackTestDailyStatService dailyStatService = SpringContextUtil.getBean(BackTestDailyStatService.class);
        BackTestPositionService positionService = SpringContextUtil.getBean(BackTestPositionService.class);
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
            // step4.1: 处理前日挂单
            orderService.deal(market, financeType, this.record.getId(), curDay, this.strategy.getBroker());
            // step4.2: 计算当日信号
            Signal signal = this.strategy.getSignal();
            // step4.3: 生成订单
            orderService.order(this.record.getId(), signal, curDay, this.strategy.getBroker());
            // step4.4: 计算日持仓收益: Tips: 这里在数据层面最好补充一个前结算价
            positionService.statDailyOpenPositionNet(this.record.getId(), this.strategy.getCurrent(), this.strategy.getPrev(), this.strategy.getBroker());
            // step4.5: 生成日统计
            dailyStatService.statDaily(this.market, this.financeType, this.record.getId(), curDay);
        }
        // 获取当前剩余资金
        log.info("[回测Id: {} -- 线程ID: {} -- 交易标的: {}] == 逐bar执行完毕", this.record.getId(), Thread.currentThread().getId(), this.code);
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
        log.info("[回测Id: {}] 回测执行成功，进行统计。 线程ID-{}",
                this.record.getId(),
                Thread.currentThread().getId());
        // step4: 对策略结果进行评估计算
        EvaluatorUtil evaluator = SpringContextUtil.getBean(EvaluatorUtil.class);
        evaluator.evaluate(this.record, this.strategy.getCurrent());

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
        this.strategy.setBroker(new Broker(market, financeType, this.initCapital));
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
