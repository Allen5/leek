package club.cybercraftman.leek.repo.trade.model.backtest;

import club.cybercraftman.leek.common.constant.trade.BackTestRecordStatus;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Table
@Entity(name = "backtest_record")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BackTestRecord {

    public static final Integer MAX_MSG_LEN = 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 执行环境
     */
    @Column(name = "running_mode", nullable = false, length = 16)
    private String runningMode;

    /**
     * 策略Id
     */
    @Column(name = "strategy_id", nullable = false)
    private String strategyId;

    @Column(name = "strategy_name", nullable = false)
    private String strategyName;

    @Column(name = "strategy_class_name", nullable = false)
    private String strategyClassName;

    /**
     * 交易标的
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * 回测时间范围
     */
    @Column(name = "start_datetime", nullable = false)
    private Date startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private Date endDateTime;

    /**
     * 回测bar记录数
     */
    @Column(name = "bars", nullable = false)
    private Integer bars;

    /**
     * 期初金额
     */
    @Column(name = "init_capital", nullable = false, precision = 18, scale = 3)
    private BigDecimal initCapital;

    /**
     * 期末金额
     */
    @Column(name = "final_capital", nullable = false, precision = 18, scale = 3)
    private BigDecimal finalCapital;

    /**
     * 回测参数值。 json格式
     */
    @Column(name = "params", nullable = false)
    private String params;

    /**
     * 回测状态
     * @see BackTestRecordStatus
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    /**
     * 回测失败信息
     */
    @Column(name = "err_message", length = 1024)
    private String errMessage;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * 回测耗时
     */
    @Column(name = "cost")
    private Long cost;

    /**
     * 收益
     */
    @Column(name = "profit", precision = 18, scale = 3)
    private BigDecimal profit;

    /**
     * 净收益
     */
    @Column(name = "net", precision = 18, scale = 3)
    private BigDecimal net;

    /**
     * 手续费、服务费
     */
    @Column(name = "commission", precision = 18, scale = 3)
    private BigDecimal commission;

    /**
     * 年化收益
     */
    @Column(name = "annualized_returns", precision = 18, scale = 4)
    private BigDecimal annualizedReturns;

    /**
     * 最大回撤
     */
    @Column(name = "max_drawdown", precision = 18, scale = 4)
    private BigDecimal maxDrawDown;

    /**
     * 最大回撤周期
     */
    @Column(name = "max_drawdown_period")
    private Integer maxDrawDownPeriod;

    /**
     * 胜率
     */
    @Column(name = "win_ratio", precision = 18, scale = 4)
    private BigDecimal winRatio;

    /**
     * 夏普比率
     */
    @Column(name = "sharp_ratio", precision = 18, scale = 4)
    private BigDecimal sharpRatio;


    /**
     * 信息比率
     */
    @Column(name = "information_ratio", precision = 18, scale = 4)
    private BigDecimal informationRatio;

    /**
     * 索诺提比率
     */
    @Column(name = "sortino_ratio", precision = 18, scale = 4)
    private BigDecimal sortinoRatio;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BackTestRecord record = (BackTestRecord) o;
        return getId() != null && Objects.equals(getId(), record.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
