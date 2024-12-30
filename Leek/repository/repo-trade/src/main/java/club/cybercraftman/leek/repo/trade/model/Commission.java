package club.cybercraftman.leek.repo.trade.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "commission")
@Data
@ToString
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "market_code", nullable = false, length = 8)
    private String marketCode;

    @Column(name = "finance_type", nullable = false, length = 8)
    private String financeType;

    /**
     * 生效环境: 回测、模拟、实盘
     * @see club.cybercraftman.leek.common.constant.trade.Environment
     */
    @Column(name = "environment", nullable = false)
    private Integer environment;

    /**
     * 补充字段，可根据用户设定
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 补充字段，可根据经纪人设定
     */
    @Column(name = "broker_id")
    private Long brokerId;

    /**
     * 补充字段，针对可按交易代码设置手续费的场景
     */
    @Column(name = "symbol", length = 16)
    private String symbol;

    /**
     * 计费种类: 印花、双边等等
     * @see club.cybercraftman.leek.common.constant.trade.CommissionCategory
     */
    @Column(name = "category", nullable = false, length = 4)
    private Integer category;

    /**
     * 计费类型：0比例 1固定
     * @see club.cybercraftman.leek.common.constant.trade.CommissionValueType
     */
    @Column(name = "type", nullable = false, length = 4)
    private Integer type;

    /**
     * 手续费值。 如果是比例，则存储的是计算后的小数点
     */
    @Column(name = "commission", nullable = false, precision = 18, scale = 6)
    private BigDecimal commission;

    /**
     * 状态: 有效，失效
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

}
