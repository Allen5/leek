package club.cybercraftman.leek.repo.trade.model.backtest;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 回测日统计表
 */
@Entity
@Table(name = "backtest_daily_stat")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BackTestDailyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    /**
     * 日期
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * 当日收益
     */
    @Column(name = "profit", nullable = false, precision = 18, scale = 3)
    private BigDecimal profit;

    /**
     * 当日净收益
     */
    @Column(name = "net", nullable = false, precision = 18, scale = 3)
    private BigDecimal net;

    /**
     * 当日无风险收益
     */
    @Column(name = "benchmark", nullable = false, precision = 18, scale = 3)
    private BigDecimal benchmark;

    /**
     * 当日手续费、服务费
     */
    @Column(name = "commission", nullable = false, precision = 18, scale = 3)
    private BigDecimal commission;

    /**
     * 当日资产总值
     */
    @Column(name = "capital", nullable = false, precision = 18, scale = 3)
    private BigDecimal capital;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestDailyStat that = (BackTestDailyStat) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
