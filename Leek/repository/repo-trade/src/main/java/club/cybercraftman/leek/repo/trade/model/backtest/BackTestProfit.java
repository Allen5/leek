package club.cybercraftman.leek.repo.trade.model.backtest;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 收益流水表
 */
@Entity
@Table(name = "backtest_profit")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BackTestProfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "symbol", nullable = false, length = 8)
    private String symbol;

    /**
     * 开仓方向
     */
    @Column(name = "direction", nullable = false, length = 4)
    private Integer direction;

    /**
     * 开仓价
     */
    @Column(name = "open_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal openPrice;

    /**
     * 平仓价
     */
    @Column(name = "close_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal closePrice;

    /**
     * 交易量(按平仓手数计算)
     */
    @Column(name = "volume", nullable = false)
    private Integer volume;

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
     * 开仓手续费
     */
    @Column(name = "open_commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal openCommission;

    /**
     * 平仓手续费
     */
    @Column(name = "close_commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal closeCommission;

    /**
     * 总手续费、服务费
     */
    @Column(name = "other_commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal otherCommission;

    /**
     * 开仓日期
     */
    @Column(name = "opened_at", nullable = false)
    private Date openedAt;

    /**
     * 平仓日期
     */
    @Column(name = "closed_at", nullable = false)
    private Date closedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestProfit that = (BackTestProfit) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
