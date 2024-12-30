package club.cybercraftman.leek.core.strategy.breakthrough;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简单移动平均线突破
 */
@Component
@Slf4j
public class SMABreakThrough extends BaseBreakThrough {

    @Override
    public boolean isUpTrend(List<BigDecimal> closePrices, BigDecimal currentClosePrice) {
        BigDecimal sma = getSMA(closePrices);
        return currentClosePrice.compareTo(sma) > 0;
    }

    @Override
    public boolean isDownTrend(List<BigDecimal> closePrices, BigDecimal currentClosePrice) {
        BigDecimal sma = getSMA(closePrices);
        return currentClosePrice.compareTo(sma) < 0;
    }

}
