package club.cybecraftman.leek.repo.financedata.model.future;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合约信息
 */
@Entity(name = "future_contract")
@Data
@ToString
public class FutureContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 品种代码
     */
    @Column(name = "product_code", nullable = false, length = 8)
    private String productCode;

    /**
     * 合约代码
     */
    @Column(name = "code", nullable = false, unique = true, length = 8)
    private String code;

    /**
     * 合约名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 上市时间
     */
    @Column(name = "list_date", nullable = false)
    private Date listDate;

    /**
     * 退市时间
     */
    @Column(name = "delist_date", nullable = false)
    private Date delistDate;

    /**
     * 最后交割日
     */
    @Column(name = "last_deliver_date", nullable = false)
    private Date lastDeliverDate;

    // TODO: 保证金比例因人而已，单独建个表维护

    /**
     * 最小报价单位
     */
    @Column(name = "price_tick", precision = 18, scale = 2)
    private BigDecimal priceTick;

    /**
     * 合约乘数
     */
    @Column(name = "multiplier", precision = 18, scale = 2)
    private BigDecimal multiplier;

    /**
     * 合约状态： TODO: 待补充
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

}
