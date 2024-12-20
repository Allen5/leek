package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.OrderStatus;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestProfit;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestOrderRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestProfitRepo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * 双边交易的基础策略
 */
@Slf4j
public abstract class TwoSideBaseStrategy extends BaseStrategy {

    @Override
    protected void onOrder(Signal signal) {
        // 开仓需要记录资金是否足够
        if ( signal.getTradeType().equals(TradeType.OPEN) ) {
            if ( !getBroker().hasEnoughCapital(signal.getPrice(), signal.getVolume(), signal.getMultiplier()) ) {
                log.warn("[回测:{}]交易日:{}, 交易标的: {}, 当前资金不足，无法挂单. [capital: {}][price: {}, volume: {}, multiplier: {}, deposite: {}]",
                        getRecordId(),
                        signal.getSymbol(),
                        getCurrent(),
                        getBroker().getCapital(),
                        signal.getPrice(),
                        signal.getVolume(),
                        signal.getMultiplier(),
                        getBroker().getDepositRatio());
                return ;
            }
            // 扣除资金. 手续费需等开仓成功后扣除
            getBroker().subDepositValue(signal.getPrice(), signal.getVolume(), signal.getMultiplier());
        }
        // 生成挂单
        this.generateOrder(signal);
    }

    @Override
    protected boolean onOpen(BackTestOrder order, CommonBar currentBar) {
        // 二次校验金额
        if ( !getBroker().hasEnoughCapital(order.getPrice(), order.getVolume(), currentBar.getMultiplier()) ) {
            log.warn("[回测:{}]交易日:{}, 交易标的: {}, 当前资金不足，无法开仓. [capital: {}][price: {}, volume: {}, multiplier: {}, deposite: {}]",
                    getRecordId(),
                    order.getSymbol(),
                    getCurrent(),
                    getBroker().getCapital(),
                    order.getPrice(),
                    order.getVolume(),
                    currentBar.getMultiplier(),
                    getBroker().getDepositRatio());
            return false;
        }
        // 创建持仓
        openPosition(order);
        // 更新资金: 扣除手续费
        getBroker().subCommission(order.getPrice(), order.getVolume(), currentBar.getMultiplier());
        return true;
    }

    @Override
    protected boolean onClose(BackTestOrder order, CommonBar currentBar) {
        // 获取持仓，按时间倒序
        List<BackTestPosition> positions = getPositions(getRecordId(), order.getSymbol(), order.getDirection());

        // 逐笔平仓
        Integer currentVolume = order.getVolume();
        IBackTestPositionRepo positionRepo = SpringContextUtil.getBean(IBackTestPositionRepo.class);
        for (BackTestPosition position : positions) {
            // 更新持仓
            int changedVolume;
            if ( currentVolume >= position.getAvailableVolume() ) {
                currentVolume = currentVolume - position.getAvailableVolume();
                changedVolume = position.getAvailableVolume();
                position.setAvailableVolume(0);
                position.setStatus(PositionStatus.CLOSE.getStatus());
            } else {
                position.setAvailableVolume(position.getAvailableVolume() -  currentVolume);
                changedVolume = currentVolume;
                currentVolume = 0;
            }
            position.setUpdatedAt(getCurrent());
            positionRepo.save(position);
            // 创建收益流水
            BackTestProfit profit = addProfit(position, currentBar.getOpen(), changedVolume, currentBar);
            //  更新资金. 只需要增加净收益即可
            getBroker().addNet(profit.getNet());
        }
        return true;
    }

    @Override
    protected void onSuccess() {
        // DO NOTHING
    }

    @Override
    protected void onFail(BackTestOrder order, CommonBar currentBar) {
        getBroker().addDepositValue(order.getPrice(), order.getVolume(), currentBar.getMultiplier());
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
     * 生成持仓记录
     * @param order
     */
    private void openPosition(final BackTestOrder order) {
        BackTestPosition position = new BackTestPosition();
        position.setRecordId(getRecordId());
        position.setMarketCode(getMarket().getCode());
        position.setFinanceType(getFinanceType().getType());
        position.setProductCode(getCode());
        position.setSymbol(order.getSymbol());
        position.setOrderId(order.getId());
        position.setDepositRatio(getBroker().getDepositRatio());
        position.setOpenPrice(order.getPrice());
        position.setOpenVolume(order.getVolume());
        position.setAvailableVolume(position.getOpenVolume());
        position.setDirection(order.getDirection());
        position.setStatus(PositionStatus.OPEN.getStatus());
        position.setCreatedAt(getCurrent());
        position.setUpdatedAt(getCurrent());

        IBackTestPositionRepo repo = SpringContextUtil.getBean(IBackTestPositionRepo.class);
        repo.save(position);
    }

    /**
     * 获取对应方向的持仓
     * @param recordId
     * @param symbol
     * @param direction
     * @return
     */
    private List<BackTestPosition> getPositions(final Long recordId, final String symbol, final Integer direction) {
        IBackTestPositionRepo repo = SpringContextUtil.getBean(IBackTestPositionRepo.class);
        return repo.findAllByRecordIdAndSymbolAndDirectionAndStatus(recordId, symbol, direction, PositionStatus.OPEN.getStatus());
    }

    /**
     * 创建收益流水
     * @param position   持仓信息
     * @param closePrice 平仓价格
     * @param closeVolume 平仓份额
     * @return
     */
    private BackTestProfit addProfit(final BackTestPosition position, final BigDecimal closePrice, final Integer closeVolume, final CommonBar currentBar) {
        BackTestProfit profit = new BackTestProfit();
        profit.setRecordId(getRecordId());
        profit.setSymbol(position.getSymbol());
        profit.setDirection(position.getDirection());
        profit.setOpenPrice(position.getOpenPrice());
        profit.setClosePrice(closePrice);
        profit.setVolume(closeVolume);

        // 根据多空方向计算收益
        BigDecimal diff;
        if (Direction.LONG.getType().equals(profit.getDirection()) ) {
            diff = profit.getClosePrice().subtract(profit.getOpenPrice());
        } else {
            diff = profit.getOpenPrice().subtract(profit.getClosePrice());
        }
        // 收益 = diff * volume * multiplier * priceTick
        BigDecimal profitValue = diff
                        .multiply(new BigDecimal(closeVolume))
                        .multiply(currentBar.getMultiplier())
                        .multiply(currentBar.getPriceTick());
        profit.setProfit(profitValue);
        profit.setOpenCommission(getBroker().getCommission(position.getOpenPrice(), position.getOpenVolume(), currentBar.getMultiplier()));
        profit.setCloseCommission(getBroker().getCommission(closePrice, closeVolume, currentBar.getMultiplier()));
        profit.setOtherCommission(BigDecimal.ZERO);

        // 净收益 = 收益 - OpenCommission - CloseCommission - OtherCommission
        BigDecimal net = profit.getProfit().subtract(profit.getOpenCommission()).subtract(profit.getCloseCommission()).subtract(profit.getOtherCommission());
        profit.setNet(net);

        profit.setOpenedAt(position.getCreatedAt());
        profit.setClosedAt(getCurrent());
        IBackTestProfitRepo repo = SpringContextUtil.getBean(IBackTestProfitRepo.class);
        profit = repo.save(profit);
        return profit;
    }


}
