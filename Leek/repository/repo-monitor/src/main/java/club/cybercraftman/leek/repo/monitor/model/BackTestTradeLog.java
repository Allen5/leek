package club.cybercraftman.leek.repo.monitor.model;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.TradeType;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 回测成交记录
 */
@Entity
@Table(name = "backtest_trade_log")
@Getter
@Setter
@RequiredArgsConstructor
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
    @Column(name = "symbol", nullable = false, length = 16)
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

    @Column(name = "net_cost", nullable = false, precision = 18, scale = 3)
    private BigDecimal netCost;

    /**
     * 交易价格
     */
    @Column(name = "price", nullable = false, precision = 18, scale = 3)
    private BigDecimal price;

    /**
     * 手续费
     */
    @Column(name = "total_commission", precision = 18, scale = 3)
    private BigDecimal totalCommission;

    /**
     * 交易时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestTradeLog that = (BackTestTradeLog) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
