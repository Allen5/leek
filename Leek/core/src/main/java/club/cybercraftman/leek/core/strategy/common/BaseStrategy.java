package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.OrderStatus;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.service.BackTestPositionService;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestOrderRepo;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
public abstract class BaseStrategy {

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
     * 计算交易信号
     * @return
     */
    public abstract Signal getSignal();

    /**
     * 根据signal下单
     */
    @Transactional
    public void order(Signal signal) throws LeekException {
        if ( null == signal ) {
            return ;
        }
        log.info("[回测:{}]交易日:{}，交易代码: {}. 信号: {}", this.recordId, this.getCurrent(), signal.getSymbol(), signal);
        // 平仓信号先进行校验
        if ( TradeType.CLOSE.equals(signal.getTradeType()) ) {
            checkPosition(signal.getSymbol());
        }
        this.onOrder(signal);
    }

    /**
     * 处理已挂单的订单
     */
    @Transactional
    public void deal() {
        IBackTestOrderRepo orderRepo = SpringContextUtil.getBean(IBackTestOrderRepo.class);
        // step1: 获取当前未处理订单
        List<BackTestOrder> orders = getOrders();
        if ( CollectionUtils.isEmpty(orders) ) {
            return ;
        }
        // step2: 逐个处理order
        for (BackTestOrder order: orders) {
            boolean result;
            // 开仓单
            if ( TradeType.OPEN.getType().equals(order.getTradeType()) ) {
                result = this.onOpen(order);
            } else {
                result = this.onClose(order);
            }
            // 根据result更新订单状态
            // Tips: 回测没有撤单一说
            order.setStatus(result ? OrderStatus.DEAL.getStatus() : OrderStatus.FAIL.getStatus());
            order.setUpdatedAt(getCurrent());
            orderRepo.save(order);
        }
    }

    /**
     * 由具体子类实现挂单逻辑
     * @param signal
     */
    protected abstract void onOrder(Signal signal);


    /**
     * 由具体子类实现开平仓逻辑
     * @param order
     */
    protected abstract boolean onOpen(BackTestOrder order);

    /**
     * 由具体子类实现开平仓逻辑
     * @param order
     */
    protected abstract boolean onClose(BackTestOrder order);

    /**
     * 成交
     */
    protected abstract void onSuccess();

    /**
     * 失败
     */
    protected abstract void onFail();


    public abstract String getId();

    public abstract String getName();

    private void checkPosition(final String symbol) throws LeekException {
        BackTestPositionService positionService = SpringContextUtil.getBean(BackTestPositionService.class);
        if ( positionService.hasPosition(this.recordId, symbol) ) {
            return;
        }
        throw new LeekException("回测记录: " + this.recordId + "的交易代码: " + symbol + "当前未持仓");
    }

    private List<BackTestOrder> getOrders() {
        IBackTestOrderRepo repo = SpringContextUtil.getBean(IBackTestOrderRepo.class);
        return repo.findAllByRecordIdAndStatus(this.recordId, OrderStatus.ORDER.getStatus());
    }


}
