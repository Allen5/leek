package club.cybercraftman.leek.repo.trade.model.backtest;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 回测的持仓数据
 */
@Entity
@Table(name = "backtest_position")
@Getter
@Setter
@RequiredArgsConstructor
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
     * 关联订单ID
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * 保证金比率
     */
    @Column(name = "deposit_ratio", nullable = false, precision = 18, scale = 4)
    private BigDecimal depositRatio;

    /**
     * 开仓价格
     */
    @Column(name = "open_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal openPrice;

    /**
     * 开仓费用
     */
    @Column(name = "open_commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal openCommission;

    /**
     * 平仓价格
     */
    @Column(name = "close_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal closePrice;

    /**
     * 平仓费用
     */
    @Column(name = "close_commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal closeCommission;

    /**
     * 其余费用
     */
    @Column(name = "other_commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal otherCommission;

    /**
     * 收益
     */
    @Column(name = "profit", nullable = false, precision = 18, scale = 3)
    private BigDecimal profit;

    /**
     * 净收益
     */
    @Column(name = "net", nullable = false, precision = 18, scale = 3)
    private BigDecimal net;

    /**
     * 持仓数量
     */
    @Column(name = "volume")
    private Integer volume;

    /**
     * 持仓形式
     * @see Direction
     */
    @Column(name = "direction", nullable = false, length = 4)
    private Integer direction;

    /**
     * 仓位状态：open，close
     * @see club.cybercraftman.leek.common.constant.trade.PositionStatus
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestPosition that = (BackTestPosition) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
