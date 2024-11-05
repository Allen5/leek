package club.cybecraftman.leek.repo.future.model;

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
    @Column(nullable = false)
    private String exchange;

    /**
     * 品种代码
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * 品种名称
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
     * 品种状态
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
