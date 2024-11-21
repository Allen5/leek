package club.cybercraftman.leek.repo.financedata.model.future;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 期货日行情信息
 */
@Getter
@Setter
@Entity
@Table(name = "future_bar_1day", indexes = {
        @Index(name = "idx_futurebar1day_datetime", columnList = "datetime, symbol")
})
@Data
@ToString
public class FutureBar1Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datetime", nullable = false)
    private Date datetime;

    /**
     * 品种代码
     */
    @Column(name = "product_code", nullable = false, length = 8)
    private String productCode;

    /**
     * 合约代码
     */
    @Column(name = "contract_code", nullable = false, length = 16)
    private String contractCode;

    /**
     * 交易代码
     */
    @Column(name = "symbol", nullable = false, length = 16)
    private String symbol;

    @Column(name = "open", precision = 18, scale = 3)
    private BigDecimal open;

    @Column(name = "high", precision = 18, scale = 3)
    private BigDecimal high;

    @Column(name = "low", precision = 18, scale = 3)
    private BigDecimal low;

    @Column(name = "close", precision = 18, scale = 3)
    private BigDecimal close;

    /**
     * 结算价
     */
    @Column(name = "settle", precision = 18, scale = 3)
    private BigDecimal settle;

    /**
     * 持仓量
     */
    @Column(name = "open_interest")
    private Long openInterest;

    /**
     * 成交量
     */
    @Column(name = "volume")
    private Long volume;

    /**
     * 成交金额
     */
    @Column(name = "amount", precision = 18, scale = 3)
    private BigDecimal amount;

}
