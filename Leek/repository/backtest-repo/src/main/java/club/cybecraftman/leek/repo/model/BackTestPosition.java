package club.cybecraftman.leek.repo.model;

import club.cybecraftman.leek.common.constant.Direction;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 回测的持仓数据
 * 每个策略 + 参数 一个持仓
 */
@Entity(name = "backtest_position")
@Data
@ToString
public class BackTestPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测的记录Id
     */
    @Column(nullable = false)
    private Long recordId;

    /**
     * 市场代码
     * @see club.cybecraftman.leek.common.constant.Market
     */
    private String marketCode;

    /**
     * 金融产品
     * @see club.cybecraftman.leek.common.constant.FinanceType
     */
    private String financeType;

    /**
     * 品种代码
     * 当金融产品为期货时，品种代码不可空
     */
    private String productCode;

    /**
     * 交易代码。 期货为合约代码，股票为股票代码
     */
    @Column(nullable = false)
    private String symbol;

    /**
     * 持仓数量
     */
    private Integer volume;

    /**
     * 持仓均价
     */
    private BigDecimal avgPrice;

    /**
     * 持仓形式
     * @see Direction
     */
    @Column(nullable = false)
    private Integer direction;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private Date updatedAt;

}
