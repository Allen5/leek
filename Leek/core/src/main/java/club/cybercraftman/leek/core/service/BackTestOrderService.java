package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.*;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.core.strategy.common.BaseStrategy;
import club.cybercraftman.leek.core.strategy.common.Signal;
import club.cybercraftman.leek.repo.financedata.BackTestDataRepo;
import club.cybercraftman.leek.repo.financedata.repository.ICalendarRepo;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestOrderRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BackTestOrderService {

    @Autowired
    private IBackTestOrderRepo orderRepo;

    @Autowired
    private BackTestCapitalCurrentService capitalCurrentService;

    @Autowired
    private BackTestDataRepo dataRepo;

    @Autowired
    private BackTestPositionService positionService;

    @Autowired
    private ICalendarRepo calendarRepo;

    /**
     * 下单
     * @param signal
     * @param datetime
     * @param broker
     */
    @Transactional
    public void order(final Long recordId, final Signal signal, final Date datetime, final Broker broker) {
        if ( null == signal ) {
            return ;
        }
        if ( !preOrderCheck(recordId, signal, datetime, broker) ) {
            return ;
        }
        makeOrder(recordId, signal, datetime, broker);
    }

    /**
     * 对当前触发止损的持仓生成挂单
     * @param recordId 回测记录ID
     */
    @Transactional
    public void stopLoss(final Long recordId, final BaseStrategy strategy) {
        // step1: 获取当前持仓
        List<BackTestPosition> positions = this.positionService.getPositions(recordId, PositionStatus.OPEN);
        if ( CollectionUtils.isEmpty(positions) ) {
            return ;
        }
        for (BackTestPosition position : positions) {
            BigDecimal price = strategy.stopLoss(position);
            if ( null == price ) {
                continue;
            }
            log.warn("[回测id: {}][持仓id: {}]触发止损，止损价格: {}", recordId, position.getId(), price);
            makeForceCloseOrder(recordId, position, price, strategy.getCurrent());
        }
    }

    /**
     * 临近最后交易日进行强平操作
     * @param recordId
     * @param datetime
     * @param broker
     * @param days
     * @throws LeekException
     */
    @Transactional
    public void forceCloseNearLastTradeDate(final Long recordId, final Date datetime, final Broker broker, final Integer days) throws LeekException {
        if ( !broker.getFinanceType().equals(FinanceType.FUTURE) ) {
            // 非期货市场不需要处理强平操作
            return ;
        }
        // step1: 获取当前持仓
        List<BackTestPosition> positions = positionService.getPositions(recordId, PositionStatus.OPEN);
        if ( CollectionUtils.isEmpty(positions) ) {
            return ;
        }
        for (BackTestPosition position: positions) {
            // 获取当前的bar
            CommonBar currentBar = dataRepo.getCurrentBar(Market.parse(position.getMarketCode()),
                    FinanceType.parse(position.getFinanceType()),
                    datetime,
                    position.getSymbol());
            // step2: 判断是否已进入实际最后交易日
            // step2.1: 计算实际最后交易日 = （最后交易日 - days） 从calendar中获取最后实际交易日
            Date realLastTradeDate = calendarRepo.findMaxDate(position.getMarketCode(), position.getFinanceType(), currentBar.getLastTradeDate(), days);
            if ( datetime.before(realLastTradeDate) ) {
                // step2.2: 若当前时间小于，则忽略。未触发强平
                continue;
            }
            // step2.3: 若 >= 最后实际交易日，且 < 退市日，此时可用当前的收盘价进行平仓挂单
            if ( datetime.compareTo(realLastTradeDate) >= 0 &&
                 datetime.compareTo(currentBar.getDelistDate()) < 0 ) {
                // 处理平仓单
                makeForceCloseOrder(recordId, position, currentBar.getClose(), datetime);
            }
            // step2.4: 若 >= 退市日，则表示该笔持仓无法平仓，结束策略运行，需要调整策略参数。
            if ( datetime.after(currentBar.getDelistDate()) ) {
                log.error("[回测id: {}][持仓: {}][当前交易日: {}][退市日:{}]无法平仓，退出回测",
                        recordId,
                        position,
                        datetime,
                        currentBar.getDelistDate());
                throw new LeekRuntimeException("无法平仓，退出回测");
            }
        }
    }

    /**
     * 可用资金为负时触发强平
     */
    @Transactional
    public void forceCloseOnNegativeCapital(final Long recordId, final Date datetime, final Broker broker) throws LeekException {
        if (!broker.getFinanceType().equals(FinanceType.FUTURE)) {
            // 非期货市场不需要处理强平操作
            return;
        }
        // 若当前资金为负，则触发强平
        if ( BigDecimal.ZERO.compareTo(broker.getCapital()) >= 0 ) {
            return ;
        }
        List<BackTestPosition> positions = positionService.getPositions(recordId, PositionStatus.OPEN);
        if ( CollectionUtils.isEmpty(positions) ) {
            return ;
        }
        log.warn("[回测id:{}]当前资金为: {}, 持仓数为: {}. 触发强平", recordId, broker.getCapital(), positions.size());
        for (BackTestPosition position : positions) {
            // 获取当前的bar
            CommonBar currentBar = dataRepo.getCurrentBar(Market.parse(position.getMarketCode()),
                    FinanceType.parse(position.getFinanceType()),
                    datetime,
                    position.getSymbol());
            // 生成平仓单
            makeForceCloseOrder(recordId, position, currentBar.getClose(), datetime);
        }
    }

    /**
     * 处理订单
     * @param market
     * @param financeType
     * @param recordId
     * @param datetime
     */
    @Transactional
    public void deal(final Market market, final FinanceType financeType, final Long recordId, final Date datetime, final Broker broker) {
        List<BackTestOrder> orders = orderRepo.findAllByRecordIdAndStatus(recordId, OrderStatus.ORDER.getStatus());
        log.info("[回测Id: {} - 交易日: {}]共有 {} 订单待处理", recordId, datetime, orders.size());
        if ( CollectionUtils.isEmpty(orders) ) {
            return ;
        }
        // 逐笔处理
        for (BackTestOrder order : orders) {
            // 1. 获取当前价格
            CommonBar bar = dataRepo.getCurrentBar(market, financeType, datetime, order.getSymbol());
            boolean result = couldDeal(recordId, order.getId(), order.getPrice(), order.getVolume(), bar);
            // 2. 处理下单失败
            if ( !result ) {
                order.setStatus(OrderStatus.FAIL.getStatus());
                order.setUpdatedAt(datetime);
                orderRepo.save(order);
                // 退回保证金
                capitalCurrentService.fallbackDeposit(recordId, datetime, order.getDeposit());
                broker.addCapital(order.getDeposit());
                if ( TradeType.CLOSE.getType().equals(order.getTradeType()) ) {
                    // Tips: 如果是平仓单，需要逆序回退orderVolume;
                    positionService.fallbackOrderVolume(recordId, order.getSymbol(), Direction.parse(order.getDirection()), order.getVolume(), datetime);
                }
                continue;
            }
            order.setStatus(OrderStatus.ORDER.getStatus());
            order.setUpdatedAt(datetime);
            orderRepo.save(order);

            // 3. 处理下单成功，进行开仓处理
            if ( TradeType.OPEN.getType().equals(order.getTradeType())) {
                positionService.open(market, financeType, recordId, order, bar);
            } else {
                // 平仓单要先回退orderVolume
                positionService.fallbackOrderVolume(recordId, order.getSymbol(), Direction.parse(order.getDirection()), order.getVolume(), datetime);
                // Tips: 平仓收益与保证金在平仓逻辑中逐笔处理
                positionService.close(recordId, order, bar, broker, datetime);
            }
            // 扣除手续费
            BigDecimal commission = broker.getCommission(order.getPrice(), order.getVolume(), bar.getMultiplier(), bar.getPriceTick());
            broker.subCapital(commission);
            capitalCurrentService.subCommission(recordId, datetime, commission);
        }
    }

    /**
     * 下单前检查逻辑
     * @param recordId
     * @param signal
     * @param datetime
     * @param broker
     * @return
     */
    private boolean preOrderCheck(final Long recordId, final Signal signal, final Date datetime, final Broker broker) {
        if ( signal.getTradeType().equals(TradeType.OPEN) ) {
            // 开仓信号需判断资金是否足够
            if ( !broker.hasEnoughCapital(signal.getPrice(), signal.getVolume(), signal.getMultiplier(), signal.getPriceTick()) ) {
                log.warn("[回测:{}]交易日:{}, 交易标的: {}, 当前资金不足，无法挂单. [capital: {}][price: {}, volume: {}, multiplier: {}, deposit: {}]",
                        recordId,
                        signal.getSymbol(),
                        datetime,
                        broker.getCapital(),
                        signal.getPrice(),
                        signal.getVolume(),
                        signal.getMultiplier(),
                        broker.getDepositRatio());
                return false;
            }
        } else {
            // 平仓信号需要校验是否存在持仓，且份额充足
            if ( !positionService.hasEnoughPosition(recordId, signal.getSymbol(), signal.getDirection(), signal.getVolume()) ) {
                log.warn("[回测:{}]交易日:{}, 交易标的: {}, 当前可用持仓不足，无法挂单. [capital: {}][price: {}, volume: {}, multiplier: {}, deposit: {}]",
                        recordId,
                        signal.getSymbol(),
                        datetime,
                        broker.getCapital(),
                        signal.getPrice(),
                        signal.getVolume(),
                        signal.getMultiplier(),
                        broker.getDepositRatio());
                return false;
            }
        }
        return true;
    }

    private void makeOrder(final Long recordId, final Signal signal, final Date datetime, final Broker broker) {
        // 生成挂单
        BackTestOrder order = new BackTestOrder();
        order.setRecordId(recordId);
        order.setSymbol(signal.getSymbol());
        order.setDirection(signal.getDirection().getType());
        order.setTradeType(signal.getTradeType().getType());
        order.setPrice(signal.getPrice());
        order.setVolume(signal.getVolume());
        order.setDeposit(broker.getDepositValue(signal.getPrice(), signal.getVolume(), signal.getMultiplier(), signal.getPriceTick()));
        order.setStatus(OrderStatus.ORDER.getStatus());
        order.setCreatedAt(datetime);
        order = orderRepo.save(order);
        // 经纪人也要扣除保证金
        broker.subCapital(order.getDeposit());
        // 生成资金流水
        capitalCurrentService.subDeposit(recordId, datetime, order.getDeposit());
        // 如果是平仓单，需要逆序更新orderVolume;
        if  (TradeType.CLOSE.getType().equals(order.getTradeType()) ) {
            positionService.addOrderVolume(recordId, signal.getSymbol(), signal.getDirection(), signal.getVolume(), datetime);
        }
    }

    /**
     * 强平触发单
     * @param recordId
     * @param position
     * @param price
     */
    private void makeForceCloseOrder(final Long recordId, final BackTestPosition position, final BigDecimal price, final Date datetime) {
        BackTestOrder order = new BackTestOrder();
        order.setRecordId(recordId);
        order.setSymbol(position.getSymbol());
        if ( Direction.LONG.getType().equals(position.getDirection()) ) {
            order.setDirection(Direction.SHORT.getType());
        } else {
            order.setDirection(Direction.LONG.getType());
        }
        order.setTradeType(TradeType.CLOSE.getType());
        order.setPrice(price);
        order.setVolume(position.getAvailableVolume());
        order.setDeposit(BigDecimal.ZERO);
        order.setStatus(OrderStatus.ORDER.getStatus());
        order.setCreatedAt(datetime);
        order = orderRepo.save(order);
        // 需要逆序更新orderVolume
        positionService.addOrderVolume(recordId, position.getSymbol(), Direction.parse(order.getDirection()), order.getVolume(), datetime);
    }

    /**
     * 判断是否可成交
     * @param recordId
     * @param orderId
     * @param orderPrice
     * @param bar
     * @return
     */
    private boolean couldDeal(final Long recordId, final Long orderId, final BigDecimal orderPrice, final Integer volume, final CommonBar bar) {
        if ( bar.getVolume() <= volume ) { // 订单量大于成交量，则无法成交
            log.error("[回测Id: {}-订单:{}][交易标的: {}][交易日: {}]成交量为{}，小于订单的成交量:{}，无法成交",
                    recordId,
                    orderId,
                    bar.getSymbol(),
                    bar.getDatetime(),
                    bar.getVolume(),
                    volume);
            return false;
        }
        boolean result = orderPrice.compareTo(bar.getLow()) >= 0 && orderPrice.compareTo(bar.getHigh()) <= 0;
        if ( !result ) {
            log.error("[回测Id: {}-订单: {}][交易标的: {}][交易日: {}]订单价: {} 不在 low: {} - high: {} 之间，无法成交",
                    recordId,
                    orderId,
                    bar.getSymbol(),
                    bar.getDatetime(),
                    orderPrice,
                    bar.getLow(),
                    bar.getHigh());
        }
        return result;
    }

}
