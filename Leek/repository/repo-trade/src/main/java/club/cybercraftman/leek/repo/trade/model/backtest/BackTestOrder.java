package club.cybercraftman.leek.repo.trade.model.backtest;

import club.cybercraftman.leek.common.constant.finance.OrderStatus;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "backtest_order")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BackTestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测记录Id
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
     */
    @Column(name = "direction", nullable = false)
    private Integer direction;

    /**
     * 交易类型: 开｜平
     */
    @Column(name = "trade_type", nullable = false)
    private Integer tradeType;

    /**
     * 挂单价格
     */
    @Column(name = "price", nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    /**
     * 保证金
     */
    @Column(name = "deposit", nullable = false, precision = 18, scale = 3)
    private BigDecimal deposit;

    /**
     * 订单状态
     * @see OrderStatus
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestOrder order = (BackTestOrder) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
