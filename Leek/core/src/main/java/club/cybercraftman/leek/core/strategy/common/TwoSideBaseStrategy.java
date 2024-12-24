package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 双边交易的基础策略
 */
@Slf4j
public abstract class TwoSideBaseStrategy extends BaseStrategy {

    @Override
    @Transactional
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
        getBroker().subCommission(getRecordId(), currentBar.getDatetime(), order.getPrice(), order.getVolume(), currentBar.getMultiplier());
        return true;
    }

    @Transactional
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

            // 计算平仓收益，计算平仓手续费，计算退回保证金

            // 更新资金. 只需要增加净收益即可
            // TODO: 这里不对，相当于开仓手续费减了两次。
            getBroker().addNet(profit.getNet());
            // 更新资金。需要加回保证金
            getBroker().addDepositValue(position.getOpenPrice(), changedVolume, currentBar.getMultiplier());
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


}
