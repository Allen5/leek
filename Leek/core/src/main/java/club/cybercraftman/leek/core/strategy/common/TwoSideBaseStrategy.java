package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.OrderStatus;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import club.cybercraftman.leek.common.constant.trade.CommissionCategory;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.repo.financedata.BackTestDataRepo;
import club.cybercraftman.leek.repo.monitor.model.BackTestTradeLog;
import club.cybercraftman.leek.repo.monitor.repository.IBackTestTradeLogRepo;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestOrderRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

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
            // 扣除资金. 手续费需等开仓成功后扣除
            getBroker().subNetCost(signal.getPrice(), signal.getVolume());
        }
        // 生成挂单
        this.generateOrder(signal);
    }

    @Override
    protected boolean onOpen(BackTestOrder order) {
        // 判断是否可成交
        BackTestDataRepo repo = SpringContextUtil.getBean(BackTestDataRepo.class);
        BigDecimal openPrice = repo.getOpenPrice(getMarket(), getFinanceType(), getCurrent(), order.getSymbol());
        if ( !couldDeal(order.getDirection(), order.getPrice(), openPrice) ) {
            log.error("订单: {} 价格与开盘价: {} 对比无法成交", order, openPrice);
            return false;
        }
        // 二次校验经验
        if ( !getBroker().hasEnoughCapital(order.getPrice(), order.getVolume()) ) {
            log.warn("[回测:{}]交易日:{}, 交易标的: {}, 当前资金不足，无法开仓. [capital: {}][price: {}, volume: {}, deposite: {}]",
                    getRecordId(),
                    order.getSymbol(),
                    getCurrent(),
                    getBroker().getCapital(),
                    order.getPrice(),
                    order.getVolume(),
                    getBroker().getDepositRatio());
            return false;
        }
        // 计算手续费
        BigDecimal netCost = getBroker().getNetCost(order.getPrice(), order.getVolume());
        BigDecimal commission = getBroker().getFee(CommissionCategory.TRADE_FEE, netCost);
        // 创建持仓
        openPosition(order, commission);
        // 更新资金
        getBroker().subCommission(commission);
        // 增加交易日志
        addLog(order, netCost, commission);
        return true;
    }

    @Override
    protected boolean onClose(BackTestOrder order) {
        // 获取持仓，按时间倒序
        // 逐笔平仓
        return false;
    }

    @Override
    protected void onSuccess() {
        // DO NOTHING
    }

    @Override
    protected void onFail(BackTestOrder order) {
        getBroker().addNetCost(order.getPrice(), order.getVolume());
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

    /**
     * 判断是否可成交
     * @param direction
     * @param orderPrice
     * @param openPrice
     * @return
     */
    private boolean couldDeal(final Integer direction, final BigDecimal orderPrice, final BigDecimal openPrice) {
        if ( Direction.LONG.getType().equals(direction) ) {
            return openPrice.compareTo(orderPrice) <= 0;
        } else {
            return openPrice.compareTo(orderPrice) >= 0;
        }
    }

    /**
     * 生成持仓记录
     * @param order
     * @param commission
     */
    private void openPosition(final BackTestOrder order, final BigDecimal commission) {
        BackTestPosition position = new BackTestPosition();
        position.setRecordId(getRecordId());
        position.setMarketCode(getMarket().getCode());
        position.setFinanceType(getFinanceType().getType());
        position.setProductCode(getCode());
        position.setSymbol(order.getSymbol());
        position.setOrderId(order.getId());
        position.setDepositRatio(getBroker().getDepositRatio());
        position.setOpenCommission(order.getPrice());
        position.setOpenCommission(commission);
        position.setClosePrice(BigDecimal.ZERO);
        position.setCloseCommission(BigDecimal.ZERO);
        position.setOtherCommission(BigDecimal.ZERO);
        position.setProfit(BigDecimal.ZERO);
        position.setVolume(order.getVolume());
        position.setDirection(order.getDirection());
        position.setStatus(PositionStatus.OPEN.getStatus());
        position.setCreatedAt(getCurrent());
        position.setUpdatedAt(getCurrent());

        IBackTestPositionRepo repo = SpringContextUtil.getBean(IBackTestPositionRepo.class);
        repo.save(position);
    }

    /**
     * 增加交易日志
     * @param order
     * @param netCost
     * @param commission
     */
    private void addLog(final BackTestOrder order, final BigDecimal netCost, final BigDecimal commission) {
        BackTestTradeLog tradeLog = new BackTestTradeLog();
        tradeLog.setRecordId(getRecordId());
        tradeLog.setSymbol(order.getSymbol());
        tradeLog.setDirection(order.getDirection());
        tradeLog.setTradeType(order.getTradeType());
        tradeLog.setPrice(order.getPrice());
        tradeLog.setVolume(order.getVolume());
        tradeLog.setNetCost(netCost);
        tradeLog.setTotalCommission(commission);
        tradeLog.setCreatedAt(getCurrent());

        IBackTestTradeLogRepo repo = SpringContextUtil.getBean(IBackTestTradeLogRepo.class);
        repo.save(tradeLog);
    }


}
