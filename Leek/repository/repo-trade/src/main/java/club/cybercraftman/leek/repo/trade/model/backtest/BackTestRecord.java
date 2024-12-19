package club.cybercraftman.leek.repo.trade.model.backtest;

import club.cybercraftman.leek.common.constant.trade.BackTestRecordStatus;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "backtest_record")
@Data
@ToString
public class BackTestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(name = "init_capital", nullable = false)
    private BigDecimal initCapital;

    /**
     * 期末金额
     */
    @Column(name = "final_capital", nullable = false)
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


}
