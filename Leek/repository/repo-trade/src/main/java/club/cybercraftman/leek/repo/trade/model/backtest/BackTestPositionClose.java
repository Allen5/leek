package club.cybercraftman.leek.repo.trade.model.backtest;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 回测平仓记录表
 */
@Entity
@Table(name = "backtest_position_close")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BackTestPositionClose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测记录id
     */
    @Column(name = "record_id", nullable = false)
    private Long recordId;

    /**
     * 平仓时间
     */
    @Column(name = "datetime", nullable = false)
    private Date datetime;

    /**
     * 持仓id
     */
    @Column(name = "position_id", nullable = false)
    private Long positionId;

    /**
     * 交易代码
     */
    @Column(name = "symbol", nullable = false, length = 16)
    private String symbol;

    /**
     * 交易方向
     */
    @Column(name = "direction", nullable = false, length = 4)
    private Integer direction;

    /**
     * 开仓价格
     */
    @Column(name = "open_price",nullable = false, precision = 18, scale = 3)
    private BigDecimal openPrice;

    /**
     * 平仓价格
     */
    @Column(name = "close_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal closePrice;

    /**
     * 平仓份额
     */
    @Column(name = "volume", nullable = false)
    private Long volume;

    /**
     * 净收益
     */
    @Column(name = "net", nullable = false, precision = 18, scale = 3)
    private BigDecimal net;

    /**
     * 手续费：包含开仓手续费
     */
    @Column(name = "commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal commission;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestPositionClose that = (BackTestPositionClose) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
