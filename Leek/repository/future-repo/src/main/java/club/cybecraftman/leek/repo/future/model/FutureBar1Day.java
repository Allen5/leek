package club.cybecraftman.leek.repo.future.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 期货日行情信息
 */
@Entity(name = "future_bar_1day")
@Data
@ToString
public class FutureBar1Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date;

    /**
     * 合约代码
     */
    @Column(nullable = false)
    private String contractCode;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    /**
     * 结算价
     */
    private BigDecimal settle;

    /**
     * 持仓量
     */
    private Long openInterest;

    /**
     * 成交量
     */
    private Long volume;

    /**
     * 成交金额
     */
    private BigDecimal amount;

}
