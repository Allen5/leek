package club.cybecraftman.leek.repo.meta.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

// TODO: 思考是否要加入user_id?以及broker_id?
@Entity
@Table(name = "meta_commission")
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
     */
    private Integer environment;

    /**
     * 计费种类: 印花、双边等等
     */
    @Column(name = "category", nullable = false, length = 4)
    private Integer category;

    /**
     * 计费类型：0比例 1固定
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
