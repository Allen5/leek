package club.cybecraftman.leek.repo.model;

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
    @Column(nullable = false)
    private String productCode;

    /**
     * 合约代码
     */
    @Column(nullable = false, unique = true)
    private String contractCode;

    /**
     * 合约名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 上市时间
     */
    private Date listDate;

    /**
     * 退市时间
     */
    private Date delistDate;

    /**
     * 最后交割日
     */
    private Date lastDeliverDate;

    // TODO: 保证金比例因人而已，单独建个表维护

    /**
     * 最小报价单位
     */
    private BigDecimal priceTick;

    /**
     * 合约乘数
     */
    private BigDecimal multiplier;

    /**
     * 合约状态： TODO: 待补充
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
