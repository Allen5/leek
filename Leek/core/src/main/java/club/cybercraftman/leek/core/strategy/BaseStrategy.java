package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.common.constant.finance.TradeType;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.service.BackTestPositionService;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;

@Slf4j
public abstract class BaseStrategy {

    /**
     * 回测记录
     */
    @Setter
    @Getter
    private Long recordId;

    /**
     * 交易标的
     */
    @Setter
    @Getter
    private String code;

    /**
     * 经纪人
     */
    @Getter
    @Setter
    private Broker broker;

    /**
     * 当前的交易时间
     */
    @Setter
    @Getter
    private Date current;

    /**
     * 策略参数
     */
    @Setter
    private Map<String, Object> params;

    /**
     * 序列化参数
     * @return
     */
    public String serializeParams() {
        if ( CollectionUtils.isEmpty(params) ) {
            return "{}";
        }
        return JSON.toJSONString(params);
    }

    /**
     * 计算交易信号
     * @return
     */
    public abstract Signal getSignal();

    /**
     * 根据signal下单
     */
    public void order(Signal signal) throws LeekException {
        if ( null == signal ) {
            return ;
        }
        log.info("[回测:{}]交易日:{}，交易代码: {}. 信号: {}", this.recordId, this.getCurrent(), signal.getSymbol(), signal);
        if ( TradeType.OPEN.equals(signal.getTradeType()) ) {
            // 处理开仓逻辑
            this.onOpen(signal);
        } else { // 处理平仓逻辑
            // 判断是否存在仓位，若不存在，则为错误信息
            checkPosition(signal.getSymbol());
            this.onClose(signal);
        }
        // TODO: 更新资金
    }

    /**
     * 处理已挂单的订单
     */
    @Transactional
    public void deal() {
        // step1: 获取当前未处理订单
        // step2: 判断是否可成交
        // step3: 更新持仓
        // step4: 更新资金、收益
    }


    /**
     * 由具体子类实现开平仓逻辑
     * @param signal
     */
    protected abstract void onOpen(Signal signal);

    /**
     * 由具体子类实现开平仓逻辑
     * @param signal
     */
    protected abstract void onClose(Signal signal);

    /**
     * 订单成交
     */
    protected abstract void onDeal();

    /**
     * 成交失败
     */
    protected abstract void onCancel();


    public abstract String getId();

    public abstract String getName();

    private void checkPosition(final String symbol) throws LeekException {
        BackTestPositionService positionService = SpringContextUtil.getBean(BackTestPositionService.class);
        if ( positionService.check(this.recordId, symbol) ) {
            return;
        }
        throw new LeekException("回测记录: " + this.recordId + "的交易代码: " + symbol + "当前未持仓");
    }


}
