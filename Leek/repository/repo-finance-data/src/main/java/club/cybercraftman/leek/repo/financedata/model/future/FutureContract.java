package club.cybercraftman.leek.repo.financedata.model.future;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合约信息
 */
@Getter
@Setter
@Entity(name = "future_contract")
@Table(indexes = {
        @Index(name = "idx_futurecontract_code", columnList = "code")
})
@Data
@ToString
public class FutureContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 交易所代码
     */
    @Column(name = "exchange_code", nullable = false, length = 8)
    private String exchangeCode;

    /**
     * 品种代码
     */
    @Column(name = "product_code", nullable = false, length = 8)
    private String productCode;

    /**
     * 合约代码
     */
    @Column(name = "code", nullable = false, unique = true, length = 16)
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

    /**
     * 散户可交易的最后一个自然日
     */
    @Column(name = "last_nature_date")
    private Date lastNatureDate;

    /**
     * 散户可交易的最后一个交易日
     */
    @Column(name = "last_trade_date")
    private Date lastTradeDate;

    /**
     * 最小报价单位
     */
    @Column(name = "price_tick", precision = 18, scale = 4)
    private BigDecimal priceTick;

    /**
     * 合约乘数
     */
    @Column(name = "multiplier", precision = 18, scale = 2)
    private BigDecimal multiplier;

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
