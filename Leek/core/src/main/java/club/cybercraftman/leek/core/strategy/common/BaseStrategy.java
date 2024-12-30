package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.*;
import club.cybercraftman.leek.common.constant.trade.StrategyParam;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.service.BackTestPositionService;
import club.cybercraftman.leek.repo.financedata.BackTestDataRepo;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

@Setter
@Slf4j
public abstract class BaseStrategy {

    @Getter
    @Setter
    private Market market;

    @Getter
    @Setter
    private FinanceType financeType;

    /**
     * 回测记录
     */
    @Getter
    private Long recordId;

    /**
     * 交易标的
     */
    @Getter
    private String code;

    /**
     * 经纪人
     */
    @Getter
    private Broker broker;

    @Getter
    private Date prev;

    /**
     * 当前的交易时间
     */
    @Getter
    private Date current;

    /**
     * 策略参数
     */
    @Setter
    private Map<String, Object> params;

    /**
     * 获取参数
     * @param key
     * @return
     * @param <T>
     */
    public <T> T getParam(final String key) {
        if ( !params.containsKey(key) ) {
            throw new LeekRuntimeException("策略运行参数" + key + "未设置");
        }
        return (T) params.get(key);
    }

    /**
     * 序列化参数
     * @return 返回序列化后的参数
     */
    public String serializeParams() {
        if ( CollectionUtils.isEmpty(params) ) {
            return "{}";
        }
        return JSON.toJSONString(params);
    }

    /**
     * 设置当前交易日,并记录上一个交易日
     * @param datetime
     */
    public void setCurrent(final Date datetime) {
        this.prev = this.current;
        this.current = datetime;
    }

    /**
     * 获取持仓服务接口
     * @return
     */
    public BackTestPositionService getPositionService() {
        return SpringContextUtil.getBean(BackTestPositionService.class);
    }

    /**
     * 获取数据接口
     * @return
     */
    public BackTestDataRepo getBackTestDataRepo() {
        return SpringContextUtil.getBean(BackTestDataRepo.class);
    }

    /**
     * 计算交易信号
     * @return
     */
    public abstract Signal getSignal();

    /**
     * 是否触发止损
     * @param position 持仓
     * @return 止损价格或NULL
     */
    public abstract BigDecimal stopLoss(final BackTestPosition position);

    /**
     * 计算可用资金
     * @return
     */
    protected BigDecimal getAvailableCapital() {
        BigDecimal rate = this.getParam(StrategyParam.TRADE_CASH_RATE.getKey());
        return this.broker.getCapital().multiply(rate);
    }

    /**
     * 计算开仓手数
     * @return
     */
    protected Long getOpenVolume(final CommonBar bar) {
        // 一个单位的保证金
        BigDecimal unitDeposit = getBroker().getDepositValue(bar.getClose(), 1L, bar.getMultiplier(), bar.getPriceTick());
        BigDecimal capital = this.getAvailableCapital();
        if ( capital.compareTo(unitDeposit) <= 0 ) {
            return 0L;
        }
        // 计算可购买手数
        return capital.divide(unitDeposit, 2, RoundingMode.DOWN).toBigInteger().longValue();
    }

    public abstract String getId();

    public abstract String getName();


}
