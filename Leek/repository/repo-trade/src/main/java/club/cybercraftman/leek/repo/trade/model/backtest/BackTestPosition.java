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
     * 交易代码。 期货为合约代码，股票为股票代码
     */
    @Column(name = "symbol", nullable = false, length = 16)
    private String symbol;

    /**
     * 关联订单ID
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;


    /**
     * 开仓保证金
     */
    @Column(name = "deposit", nullable = false, precision = 18, scale = 3)
    private BigDecimal deposit;

    /**
     * 可用保证金
     */
    @Column(name = "available_deposit", nullable = false, precision = 18, scale = 3)
    private BigDecimal availableDeposit;

    /**
     * 开仓价格
     */
    @Column(name = "open_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal openPrice;

    /**
     * 已开仓份额
     */
    @Column(name = "open_volume", nullable = false)
    private Long openVolume;

    /**
     * 可用份额 = 开仓份额 - 挂单份额
     */
    @Column(name = "available_volume", nullable = false)
    private Long availableVolume;

    /**
     * 挂单份额
     */
    @Column(name = "order_volume", nullable = false)
    private Long orderVolume;

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

    // 可用份额 = 记录的可用份额 - 订单占用份额
    public Long getAvailableVolume() {
        return this.availableVolume - this.orderVolume;
    }

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
