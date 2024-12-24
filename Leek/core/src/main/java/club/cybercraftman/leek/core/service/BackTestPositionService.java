package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BackTestPositionService {

    @Autowired
    private IBackTestPositionRepo backTestPositionRepo;

    @Autowired
    private BackTestCapitalCurrentService capitalCurrentService;

    /**
     * 检查是否有持仓
     * @param recordId
     * @param symbol
     * @return
     */
    public boolean hasEnoughPosition(Long recordId, String symbol, Direction direction, Integer volume) {
        Long availableVolume = backTestPositionRepo.sumVolumeByRecordIdAndSymbolAndDirectionAndStatus(recordId, symbol, direction.getType(), PositionStatus.OPEN.getStatus());
        return availableVolume != null && availableVolume >= volume;
    }

    /**
     * 倒序更新持仓的订单份额
     * @param recordId
     * @param totalVolume
     */
    @Transactional
    public void addOrderVolume(final Long recordId, final String symbol, final Direction direction, final Integer totalVolume, final Date datetime) {
        // Tips: 这里已经完成倒序排序
        List<BackTestPosition> positions = backTestPositionRepo.findAllByRecordIdAndSymbolAndDirectionAndStatus(recordId, symbol, direction.getType(), PositionStatus.OPEN.getStatus());
        if ( CollectionUtils.isEmpty(positions) ) {
            throw new LeekRuntimeException("校验已成功，仍旧未获取到持仓信息。请检查程序. recordId: " + recordId + " symbol: " + symbol + ", direction: " + direction);
        }
        Integer currentVolume = totalVolume;
        for (BackTestPosition position : positions) {
            Integer changeVolume;
            if ( currentVolume >= position.getAvailableVolume() ) {
                changeVolume = position.getAvailableVolume();
                currentVolume = currentVolume - changeVolume;
            } else {
                changeVolume = currentVolume;
                currentVolume = 0;
            }
            position.setOrderVolume(position.getOrderVolume() + changeVolume);
            position.setUpdatedAt(datetime);
            backTestPositionRepo.save(position);
        }
    }

    /**
     * 回退orderVolume
     * @param recordId
     * @param symbol
     * @param direction
     */
    @Transactional
    public void fallbackOrderVolume(final Long recordId, final String symbol, final Direction direction, final Integer totalVolume, final Date datetime) {
        List<BackTestPosition> positions = backTestPositionRepo.findAllByRecordIdAndSymbolAndDirectionAndStatus(recordId, symbol, direction.getType(), PositionStatus.OPEN.getStatus());
        if ( CollectionUtils.isEmpty(positions) ) {
            return ;
        }
        Integer currentVolume = totalVolume;
        for (BackTestPosition position : positions) {
            if ( position.getOpenVolume() == 0 ) {
                continue;
            }
            if ( currentVolume >= position.getOrderVolume() ) {
                currentVolume = currentVolume - position.getOrderVolume();
                position.setOrderVolume(0);
            } else {
                position.setOrderVolume(position.getOrderVolume() - currentVolume);
                currentVolume = 0;
            }
            position.setOrderVolume(0);
            position.setUpdatedAt(datetime);
            backTestPositionRepo.save(position);
        }
    }

    @Transactional
    public void open(final Market market, final FinanceType financeType, final Long recordId, final BackTestOrder order, final CommonBar bar) {
        BackTestPosition position = new BackTestPosition();
        position.setRecordId(recordId);
        position.setMarketCode(market.getCode());
        position.setFinanceType(financeType.getType());
        position.setSymbol(order.getSymbol());
        position.setOrderId(order.getId());
        position.setDeposit(order.getDeposit());
        position.setAvailableDeposit(order.getDeposit());
        position.setOpenPrice(order.getPrice());
        position.setOpenVolume(order.getVolume());
        position.setAvailableVolume(order.getVolume());
        position.setOrderVolume(0);
        position.setDirection(order.getDirection());
        position.setStatus(PositionStatus.OPEN.getStatus());
        position.setCreatedAt(bar.getDatetime());
        position.setUpdatedAt(bar.getDatetime());
        backTestPositionRepo.save(position);
    }

    @Transactional
    public void close(final Long recordId, final BackTestOrder order, final CommonBar bar, final Broker broker, final Date datetime) {
        // 获取当前持仓列表
        List<BackTestPosition> positions = backTestPositionRepo.findAllByRecordIdAndSymbolAndDirectionAndStatus(recordId, order.getSymbol(), order.getDirection(), PositionStatus.OPEN.getStatus());
        if ( CollectionUtils.isEmpty(positions) ) {
            log.error("未找到对应的持仓信息. recordId: {}, symbol: {}, direction: {}", recordId, order.getSymbol(), order.getDirection());
            return ;
        }
        // 逐笔平仓
        Integer totalVolume = order.getVolume();
        for (BackTestPosition position : positions) {
            Integer changeVolume;
            if (totalVolume >= position.getAvailableVolume()) {
                changeVolume = position.getAvailableVolume();
                totalVolume = totalVolume - changeVolume;
                position.setAvailableVolume(0);
                position.setStatus(PositionStatus.CLOSE.getStatus());
            } else {
                changeVolume = totalVolume;
                position.setAvailableVolume(position.getAvailableVolume() - totalVolume);
                totalVolume = 0;
            }
            // 计算平仓收益
            BigDecimal profit = calcProfit(position, bar, changeVolume);
            broker.addCapital(profit);
            capitalCurrentService.addProfit(recordId, datetime, profit);

            // 计算平仓手续费
            BigDecimal commission = broker.getCommission(order.getPrice(), changeVolume, bar.getMultiplier(), bar.getPriceTick());
            broker.subCapital(commission);
            capitalCurrentService.subCommission(recordId, datetime, commission);

            // 计算退回的保证金
            BigDecimal deposit = position.getDeposit();
            if ( position.getAvailableVolume() > 0 ) {
                BigDecimal factor = BigDecimal.valueOf(changeVolume).divide(BigDecimal.valueOf(position.getOpenVolume()), 2, RoundingMode.HALF_UP);
                deposit = deposit.multiply(factor);
            }
            broker.addCapital(deposit);
            capitalCurrentService.fallbackDeposit(recordId, datetime, deposit);

            position.setAvailableDeposit(position.getAvailableDeposit().subtract(deposit));
            position.setUpdatedAt(datetime);
            backTestPositionRepo.save(position);
        }
    }

    private BigDecimal calcProfit(final BackTestPosition position, final CommonBar bar, final Integer volume) {
        if ( Direction.LONG.getType().equals(position.getDirection()) ) {
            return bar.getClose().subtract(position.getOpenPrice()).multiply(BigDecimal.valueOf(volume)).multiply(bar.getPriceTick());
        } else {
            return position.getOpenPrice().subtract(bar.getClose()).multiply(BigDecimal.valueOf(volume)).multiply(bar.getMultiplier());
        }
    }

}
