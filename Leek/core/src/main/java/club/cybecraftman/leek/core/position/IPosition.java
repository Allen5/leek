package club.cybecraftman.leek.core.position;

import club.cybecraftman.leek.common.constant.Direction;

import java.math.BigDecimal;

/**
 * 持仓
 */
public interface IPosition {

    /**
     * 开仓
     * @param symbol
     * @param count
     * @param price
     */
    void open(final String symbol,
              final Direction direction,
              final Integer count,
              final BigDecimal price);

    /**
     * 平仓
     * @param symbol 交易代码
     */
    void close(final String symbol, final BigDecimal price);

}
