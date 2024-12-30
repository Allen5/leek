package club.cybercraftman.leek.repo.financedata.model.future;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 期货回测数据集. 通过ETL得到
 */
@Entity
@Table(name = "ods_future_backtest")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class FutureBackTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datetime", nullable = false)
    private Date datetime;

    /**
     * 品种代码
     */
    @Column(name = "product_code", nullable = false, length = 8)
    private String productCode;

    /**
     * 合约代码
     */
    @Column(name = "contract_code", nullable = false, length = 16)
    private String contractCode;

    /**
     * 交易代码
     */
    @Column(name = "symbol", nullable = false, length = 16)
    private String symbol;

    @Column(name = "open", precision = 18, scale = 3)
    private BigDecimal open;

    @Column(name = "high", precision = 18, scale = 3)
    private BigDecimal high;

    @Column(name = "low", precision = 18, scale = 3)
    private BigDecimal low;

    @Column(name = "close", precision = 18, scale = 3)
    private BigDecimal close;

    /**
     * 结算价
     */
    @Column(name = "settle", precision = 18, scale = 3)
    private BigDecimal settle;

    /**
     * 持仓量
     */
    @Column(name = "open_interest")
    private Long openInterest;

    /**
     * 成交量
     */
    @Column(name = "volume")
    private Long volume;

    /**
     * 成交金额
     */
    @Column(name = "amount", precision = 18, scale = 3)
    private BigDecimal amount;

    /**
     * 在当前的交易中，该合约是否为主力合约
     */
    @Column(name = "main_contract", nullable = false, length = 4)
    private Integer isMainContract;

    /**
     * 合约乘数
     */
    @Column(name = "multiplier", nullable = false, precision = 18, scale = 3)
    private BigDecimal multiplier;

    /**
     * 最小变动价位
     */
    @Column(name = "price_tick", nullable = false, precision = 18, scale = 3)
    private BigDecimal priceTick;

    /**
     * 最后交易日
     */
    @Column(name = "last_trade_date", nullable = false)
    private Date lastTradeDate;

    /**
     * 退市日
     */
    @Column(name = "delist_date", nullable = false)
    private Date delistDate;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FutureBackTest that = (FutureBackTest) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
