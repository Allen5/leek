package club.cybercraftman.leek.repo.trade.model.backtest;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 回测的持仓数据
 * 每个策略 + 参数 一个持仓
 */
@Entity
@Table(name = "backtest_position")
@Data
@ToString
public class BackTestPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测的记录Id
     */
    @Column(name = "record_id", nullable = false, length = 32)
    private Long recordId;

    /**
     * 市场代码
     * @see Market
     */
    @Column(name = "market_code", nullable = false, length = 8)
    private String marketCode;

    /**
     * 金融产品
     * @see FinanceType
     */
    @Column(name = "finance_type", nullable = false, length = 8)
    private String financeType;

    /**
     * 品种代码
     * 当金融产品为期货时，品种代码不可空
     */
    @Column(name = "product_code", nullable = false, length = 8)
    private String productCode;

    /**
     * 交易代码。 期货为合约代码，股票为股票代码
     */
    @Column(name = "symbol", nullable = false, length = 8)
    private String symbol;

    /**
     * 持仓数量
     */
    @Column(name = "volume")
    private Integer volume;

    /**
     * 持仓均价
     */
    @Column(name = "avg_price", precision = 18, scale = 3)
    private BigDecimal avgPrice;

    /**
     * 持仓形式
     * @see Direction
     */
    @Column(name = "direction", nullable = false, length = 4)
    private Integer direction;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

}
