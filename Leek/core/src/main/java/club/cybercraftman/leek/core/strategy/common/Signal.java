package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class Signal {

    /**
     * 回测记录id
     */
    private Long recordId;

    /**
     * 交易代码
     */
    private String symbol;

    /**
     * 交易方向: 空｜多
     */
    private Direction direction;

    /**
     * 交易类型: 开｜平
     */
    private TradeType tradeType;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 量
     */
    private Integer volume;

    /**
     * 乘数
     */
    private BigDecimal multiplier;

    /**
     * 最小变动价位
     */
    private BigDecimal priceTick;

}
