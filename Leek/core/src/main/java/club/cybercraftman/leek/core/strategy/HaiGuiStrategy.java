package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.trade.StrategyParam;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
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

    // TODO: 未处理资金、风险管理

    @Override
    public Signal getSignal() {
        // Tips: 这里有问题，如果合约已经切换，这里获取的合约是不对的。 需要先根据持仓计算下是否需要移仓换月
//        List<BackTestPosition> positions = getPositionService().getPositions(getMarket(), getFinanceType(), getCode());
//
//        // 获取当日的交易代码，期货位当日的主力合约
//        String symbol = getBackTestDataRepo().getSymbol(getMarket(), getFinanceType(), getCode(), getCurrent());
//        if ( null == symbol ) {
//            log.error("获取当日的交易代码失败. [market: {}, financeType: {}, code: {}, current: {}]",
//                    getMarket().getCode(), getFinanceType().getType(), getCode(), getCurrent());
//            throw new LeekRuntimeException("获取当日的交易代码失败.");
//        }
//
//        // TODO:处理强平信号：达到最后交易日，进行平仓
//
//        // 处理移仓换月
//        if ( !CollectionUtils.isEmpty(positions)) {
//            // 需要处理移仓换月
//        }
//
//        // 获取当日的收盘价
//        CommonBar bar = getBackTestDataRepo().getCurrentBar(getMarket(), getFinanceType(), getCurrent(), symbol);
//        if ( null == bar ) {
//            log.error("获取当日的收盘价失败. [market: {}, financeType: {}, code: {}, current: {}]",
//                    getMarket().getCode(), getFinanceType().getType(), getCode(), getCurrent());
//            throw new LeekRuntimeException("获取当日的收盘价失败.");
//        }
//        // 获取前日开始的，N个交易日的收盘价
//        Integer period = this.<Integer>getParam(StrategyParam.PERIOD.getKey());
//        List<CommonBar> bars = getBackTestDataRepo().getPeriodBars(getMarket(), getFinanceType(), getPrev(), symbol, period);
//
//        List<BigDecimal> closePrices = bars.stream().map(CommonBar::getClose).collect(Collectors.toList());
//
//        // 通过突破方法计算
//        BaseBreakThrough breakThrough = SpringContextUtil.getBean(this.<String>getParam(StrategyParam.BREAKTHROUGH_CLZ.getKey()));
//        breakThrough.clearOptions();
//        breakThrough.option(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey(), this.<BigDecimal>getParam(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey()));
//
//        // 判断是否有趋势
//        if ( breakThrough.isUpTrend(closePrices, bar.getClose()) ) {
//            // 根据持仓产生信号. 开多｜平空
//        } else if ( breakThrough.isDownTrend(closePrices, bar.getClose()) ) {
//            // 根据持仓产生信号. 开空｜平多
//        }

        return null;
    }

    @Override
    public BigDecimal stopLoss(BackTestPosition position) {
        return null;
    }

    @Override
    public String getId() {
        return "HaiGui";
    }

    @Override
    public String getName() {
        return "海龟策略-简易布林带突破";
    }

}
