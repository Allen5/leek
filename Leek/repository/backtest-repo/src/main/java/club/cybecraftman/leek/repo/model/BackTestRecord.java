package club.cybecraftman.leek.repo.model;

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
    @Column(nullable = false)
    private Long strategyId;

    /**
     * 期初金额
     */
    @Column(nullable = false)
    private BigDecimal initCapital;

    /**
     * 期末金额
     */
    @Column(nullable = false)
    private BigDecimal finalCapital;

    /**
     * 回测参数值。 json格式
     */
    @Column(nullable = false)
    private String params;

    /**
     * 回测状态
     * @see club.cybecraftman.leek.repo.constant.BackTestRecordStatus
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
