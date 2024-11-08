package club.cybecraftman.leek.repo.monitor.model;

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
    @Column(name = "record_id", nullable = false, length = 32)
    private Long recordId;

    /**
     * 交易代码
     */
    @Column(name = "symbol", nullable = false, length = 8)
    private String symbol;

    /**
     * 交易方向: 空｜多
     * @see Direction
     */
    @Column(name = "direction", nullable = false, length = 4)
    private Integer direction;

    /**
     * 交易类型：开仓｜平仓
     * @see TradeType
     */
    @Column(name = "trade_type", nullable = false, length = 4)
    private Integer tradeType;

    /**
     * 数量
     */
    @Column(name = "volume", nullable = false)
    private Integer volume;

    /**
     * 交易价格
     */
    @Column(name = "price", nullable = false, precision = 18, scale = 3)
    private BigDecimal price;

    /**
     * 交易时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

}
