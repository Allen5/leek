package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.constant.trade.StrategyParam;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.core.strategy.breakthrough.BaseBreakThrough;
import club.cybercraftman.leek.core.strategy.common.BaseStrategy;
import club.cybercraftman.leek.core.strategy.common.Signal;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class HaiGuiStrategy extends BaseStrategy {

    @Override
    public Signal getSignal() {

        // 获取当日的行情数据，期货为当日的主力合约
        CommonBar currentBar = getBackTestDataRepo().getMainCurrentBar(getMarket(), getFinanceType(), getCode(), getCurrent());
        // 获取前日开始的，N个交易日的收盘价
        Integer period = this.<Integer>getParam(StrategyParam.PERIOD.getKey());
        List<CommonBar> bars = getBackTestDataRepo().getPeriodBars(getMarket(), getFinanceType(), getPrev(), currentBar.getSymbol(), period);
        if ( CollectionUtils.isEmpty(bars) ) {
            log.warn("[回测id: {}][交易代码: {}][周期: {}]行情数据数量为空", getRecordId(), currentBar.getSymbol(), period);
            return null;
        }
        if ( bars.size() != period ) {
            log.warn("[回测id: {}][交易代码: {}][周期: {}]行情数据数量: {} 不足", getRecordId(), currentBar.getSymbol(), period, bars.size());
            return null;
        }
        List<BigDecimal> closePrices = bars.stream().map(CommonBar::getClose).collect(Collectors.toList());

        // 通过突破方法计算
        BaseBreakThrough breakThrough = SpringContextUtil.getBean(this.<String>getParam(StrategyParam.BREAKTHROUGH_CLZ.getKey()));
        breakThrough.clearOptions();
        breakThrough.option(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey(), this.<BigDecimal>getParam(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey()));

        // 判断是否有趋势
        Signal signal = null;
        List<BackTestPosition> positions = getPositionService().getPositions(getRecordId(), currentBar.getSymbol(), PositionStatus.OPEN);
        // 多头持仓列表
        List<BackTestPosition> longPositions = positions.stream().filter(p -> Direction.LONG.getType().equals(p.getDirection())).collect(Collectors.toList());
        // 空头持仓列表
        List<BackTestPosition> shortPositions = positions.stream().filter(p -> Direction.SHORT.getType().equals(p.getDirection())).collect(Collectors.toList());
        if ( breakThrough.isUpTrend(closePrices, currentBar.getClose()) ) {
            // 根据持仓产生信号. 开多 | 增多 ｜ 平空
            if ( positions.isEmpty() ) { // 空仓，则生成开多信号
                Long volume = getOpenVolume(currentBar);
                if ( volume == 0 ) {
                    return null;
                }
                signal = new Signal();
                signal.setRecordId(getRecordId());
                signal.setSymbol(currentBar.getSymbol());
                signal.setDirection(Direction.LONG);
                signal.setTradeType(TradeType.OPEN);
                signal.setPrice(currentBar.getClose());
                signal.setVolume(volume);// 计算可购买份额
                signal.setMultiplier(currentBar.getMultiplier());
                signal.setPriceTick(currentBar.getPriceTick());
                return signal;
            }
            // 优先平空
            if ( !shortPositions.isEmpty() ) {
                long volume = shortPositions.stream().mapToLong(BackTestPosition::getAvailableVolume).sum();
                signal = new Signal();
                signal.setRecordId(getRecordId());
                signal.setSymbol(currentBar.getSymbol());
                signal.setDirection(Direction.SHORT);
                signal.setTradeType(TradeType.CLOSE);
                signal.setPrice(currentBar.getClose());
                signal.setVolume(volume);
                signal.setMultiplier(currentBar.getMultiplier());
                signal.setPriceTick(currentBar.getPriceTick());
                return signal;
            }
            // 处理增多
            if ( !longPositions.isEmpty() ) {
                Integer limitCount = this.<Integer>getParam(StrategyParam.INCREASE_POSITION_LIMIT_COUNT.getKey());
                if ( longPositions.size() >= limitCount ) {
                    log.warn("[回测id: {}][交易日:{}]当前已开仓:{}次，超过限制: {}。不再加仓", getRecordId(),
                            currentBar.getDatetime(), longPositions.size(), limitCount);
                    return null;
                }
                Long volume = getOpenVolume(currentBar);
                if ( volume == 0 ) {
                    return null;
                }
                signal = new Signal();
                signal.setRecordId(getRecordId());
                signal.setSymbol(currentBar.getSymbol());
                signal.setDirection(Direction.LONG);
                signal.setTradeType(TradeType.OPEN);
                signal.setPrice(currentBar.getClose());
                signal.setVolume(volume);// 计算可购买份额
                signal.setMultiplier(currentBar.getMultiplier());
                signal.setPriceTick(currentBar.getPriceTick());
                return signal;
            }
        } else if ( breakThrough.isDownTrend(closePrices, currentBar.getClose()) ) {
            // 根据持仓产生信号. 开空 ｜ 增空 ｜ 平多
            if ( positions.isEmpty() ) {
                Long volume = getOpenVolume(currentBar);
                if ( volume == 0 ) {
                    return null;
                }
                signal = new Signal();
                signal.setRecordId(getRecordId());
                signal.setSymbol(currentBar.getSymbol());
                signal.setDirection(Direction.SHORT);
                signal.setTradeType(TradeType.OPEN);
                signal.setPrice(currentBar.getClose());
                signal.setVolume(volume);// 计算可购买份额
                signal.setMultiplier(currentBar.getMultiplier());
                signal.setPriceTick(currentBar.getPriceTick());
                return signal;
            }
            // 优先平多
            if ( !longPositions.isEmpty() ) {
                long volume = longPositions.stream().mapToLong(BackTestPosition::getAvailableVolume).sum();
                signal = new Signal();
                signal.setRecordId(getRecordId());
                signal.setSymbol(currentBar.getSymbol());
                signal.setDirection(Direction.LONG);
                signal.setTradeType(TradeType.CLOSE);
                signal.setPrice(currentBar.getClose());
                signal.setVolume(volume);
                signal.setMultiplier(currentBar.getMultiplier());
                signal.setPriceTick(currentBar.getPriceTick());
                return signal;
            }
            // 处理增空
            if ( !shortPositions.isEmpty() ) {
                Integer limitCount = this.<Integer>getParam(StrategyParam.INCREASE_POSITION_LIMIT_COUNT.getKey());
                if ( shortPositions.size() >= limitCount ) {
                    log.warn("[回测id: {}][交易日:{}]当前已开仓:{}次，超过限制: {}。不再加仓", getRecordId(),
                            currentBar.getDatetime(), shortPositions.size(), limitCount);
                    return null;
                }
                Long volume = getOpenVolume(currentBar);
                if ( volume == 0 ) {
                    return null;
                }
                signal = new Signal();
                signal.setRecordId(getRecordId());
                signal.setSymbol(currentBar.getSymbol());
                signal.setDirection(Direction.SHORT);
                signal.setTradeType(TradeType.OPEN);
                signal.setPrice(currentBar.getClose());
                signal.setVolume(volume);// 计算可购买份额
                signal.setMultiplier(currentBar.getMultiplier());
                signal.setPriceTick(currentBar.getPriceTick());
                return signal;
            }
        }
        return signal;
    }

    @Override
    public BigDecimal stopLoss(BackTestPosition position) {
        // TODO: 计算止损
        return null;
    }

    @Override
    public String getId() {
        return "HaiGui";
    }

    @Override
    public String getName() {
        return "海龟策略";
    }

}
