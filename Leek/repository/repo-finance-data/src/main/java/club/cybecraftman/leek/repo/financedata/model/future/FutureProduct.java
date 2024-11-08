package club.cybecraftman.leek.repo.financedata.model.future;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * 期货品种
 */
@Entity(name = "future_product")
@Data
@ToString
public class FutureProduct {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    /**
     * 交易所代码
     */
    @Column(name = "exchange_code", nullable = false, length = 8)
    private String exchangeCode;

    /**
     * 品种代码
     */
    @Column(name = "code", nullable = false, unique = true, length = 8)
    private String code;

    /**
     * 品种名称
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
     * 品种状态
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
