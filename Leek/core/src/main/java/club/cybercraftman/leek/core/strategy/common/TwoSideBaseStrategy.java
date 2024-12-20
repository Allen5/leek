package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.constant.finance.OrderStatus;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestOrderRepo;
import lombok.extern.slf4j.Slf4j;

/**
 * 双边交易的基础策略
 */
@Slf4j
public abstract class TwoSideBaseStrategy extends BaseStrategy {

    @Override
    protected void onOrder(Signal signal) {
        // 开仓需要记录资金是否足够
        if ( signal.getTradeType().equals(TradeType.OPEN) ) {
            if ( !getBroker().hasEnoughCapital(signal.getPrice(), signal.getVolume()) ) {
                log.warn("[回测:{}]交易日:{}, 交易标的: {}, 当前资金不足，无法挂单. [capital: {}][price: {}, volume: {}, deposite: {}]",
                        getRecordId(),
                        signal.getSymbol(),
                        getCurrent(),
                        getBroker().getCapital(),
                        signal.getPrice(),
                        signal.getVolume(),
                        getBroker().getDepositRatio());
                return ;
            }
            // TODO: 这里要把资金减去。
        }
        // 生成挂单
        this.generateOrder(signal);
    }

    @Override
    protected boolean onOpen(BackTestOrder order) {
        // TODO: 获取当前bar的开盘价
        return false;
    }

    @Override
    protected boolean onClose(BackTestOrder order) {
        return false;
    }

    @Override
    protected void onSuccess() {

    }

    @Override
    protected void onFail() {

    }

    private void generateOrder(Signal signal) {
        BackTestOrder order = new BackTestOrder();
        order.setRecordId(this.getRecordId());
        order.setSymbol(signal.getSymbol());
        order.setDirection(signal.getDirection().getType());
        order.setTradeType(signal.getTradeType().getType());
        order.setPrice(signal.getPrice());
        order.setVolume(signal.getVolume());
        order.setStatus(OrderStatus.ORDER.getStatus());
        order.setCreatedAt(getCurrent());
        order.setUpdatedAt(getCurrent());
        // 生成订单
        IBackTestOrderRepo repo = SpringContextUtil.getBean(IBackTestOrderRepo.class);
        repo.save(order);
    }


}
