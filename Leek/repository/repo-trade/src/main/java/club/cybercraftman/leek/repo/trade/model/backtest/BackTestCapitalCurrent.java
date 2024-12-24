package club.cybercraftman.leek.repo.trade.model.backtest;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 回测资金流水表
 */
@Entity
@Table(name = "backtest_capital_current")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BackTestCapitalCurrent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测记录Id
     */
    @Column(name = "record_id", nullable = false)
    private Long recordId;

    /**
     * 变更时间
     */
    @Column(name = "datetime", nullable = false)
    private Date datetime;

    /**
     * 资金流水类型
     * @see club.cybercraftman.leek.common.constant.trade.CapitalCurrentType
     */
    @Column(name = "type", nullable = false, length = 4)
    private Integer type;

    /**
     * 变更类目
     * @see club.cybercraftman.leek.common.constant.trade.CapitalCurrentCategory
     */
    @Column(name = "category", nullable = false, length = 4)
    private Integer category;

    /**
     * 变更金额.
     */
    @Column(name = "amount", nullable = false, precision = 18, scale = 3)
    private BigDecimal amount;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestCapitalCurrent that = (BackTestCapitalCurrent) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
