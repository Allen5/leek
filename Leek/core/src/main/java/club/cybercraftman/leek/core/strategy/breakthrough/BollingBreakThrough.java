package club.cybercraftman.leek.core.strategy.breakthrough;

import club.cybercraftman.leek.common.constant.trade.StrategyParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 布林带突破
 * Tips: 如何避免布林带假信号？
 */
@Component("BollingBreakThrough")
@Slf4j
public class BollingBreakThrough extends BaseBreakThrough {

    @Override
    public boolean isUpTrend(List<BigDecimal> closePrices, BigDecimal currentClosePrice) {
        // 计算SMA
        BigDecimal sma = getSMA(closePrices);
        // 计算标准差
        BigDecimal sd = getStandardDeviation(closePrices, sma);
        BigDecimal factor = this.getOption(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey());
        // 计算上轨
        BigDecimal up = sma.add(sd.multiply(factor));
        // Tips: 这里再增加动量因子计算
        return currentClosePrice.compareTo(up) > 0; // 当前价格超过上轨，表示形成向上突破
    }

    @Override
    public boolean isDownTrend(List<BigDecimal> closePrices, BigDecimal currentClosePrice) {
        // 计算SMA
        BigDecimal sma = getSMA(closePrices);
        // 计算标准差
        BigDecimal sd = getStandardDeviation(closePrices, sma);
        BigDecimal factor = this.getOption(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey());
        // 计算下轨
        BigDecimal down = sma.subtract(sd.multiply(factor)); // 计算下轨
        // Tips: 这里再增加动量因子计算
        return currentClosePrice.compareTo(down) < 0; // 当前价格超过上轨，表示形成向上突破
    }

    /**
     * 计算标准差
     * @param closePrices
     * @param sma
     * @return
     */
    private BigDecimal getStandardDeviation(List<BigDecimal> closePrices, BigDecimal sma) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal closePrice : closePrices) {
            BigDecimal diff = closePrice.subtract(sma).pow(2);
            total = total.add(diff);
        }
        total = total.divide(BigDecimal.valueOf(closePrices.size()), 2, RoundingMode.HALF_UP);
        total = BigDecimal.valueOf(Math.sqrt(total.doubleValue()));
        return total;
    }

}
