package club.cybecraftman.leek.repo.backtest.model;

import club.cybecraftman.leek.common.constant.finance.Direction;
import club.cybecraftman.leek.common.constant.finance.TradeType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 回测成交记录
 */
@Entity(name = "backtest_trade_log")
@Data
@ToString
public class BackTestTradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测记录id
     */
    @Column(nullable = false)
    private Long recordId;

    /**
     * 交易代码
     */
    @Column(nullable = false)
    private String symbol;

    /**
     * 交易方向: 空｜多
     * @see Direction
     */
    private Integer direction;

    /**
     * 交易类型：开仓｜平仓
     * @see TradeType
     */
    private Integer tradeType;

    /**
     * 数量
     */
    private Integer volume;

    /**
     * 交易价格
     */
    private BigDecimal price;

    /**
     * 交易时间
     */
    private Date createdAt;

}
