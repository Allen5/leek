package club.cybercraftman.leek.core.strategy.breakthrough;

import java.math.BigDecimal;
import java.util.List;

/**
 * 趋势突破计算方法
 */
public interface IBreakThrough {

    /**
     * 是否向上突破形成趋势
     * @param closePrices 收盘价列表
     * @param currentClosePrice 当前收盘价
     * @return boolean
     */
    boolean isUpTrend(List<BigDecimal> closePrices, BigDecimal currentClosePrice);

    /**
     * 是否向下突破形成趋势
     * @param closePrices 收盘价列表
     * @param currentClosePrice 当前收盘价
     * @return boolean
     */
    boolean isDownTrend(List<BigDecimal> closePrices, BigDecimal currentClosePrice);

}
