package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.*;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

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
    private Map<String, Object> params;

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
     * 计算交易信号
     * @return
     */
    public abstract Signal getSignal();

    /**
     * 由具体子类实现开平仓逻辑
     * @param order
     */
    protected abstract boolean onClose(BackTestOrder order, CommonBar currentBar);

    public abstract String getId();

    public abstract String getName();

}
