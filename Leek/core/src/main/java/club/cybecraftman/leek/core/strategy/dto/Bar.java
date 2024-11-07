package club.cybecraftman.leek.core.strategy.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class Bar {

    /**
     * 交易日
     */
    private Date datetime;

    /**
     * 交易代码
     */
    private String symbol;

    /**
     * 开盘价
     */
    private BigDecimal open;

    /**
     * 最高价
     */
    private BigDecimal high;

    /**
     * 最低价
     */
    private BigDecimal low;

    /**
     * 收盘价
     */
    private BigDecimal close;

    /**
     * 持仓量
     */
    private Long openInterest;

    /**
     * 成交量
     */
    private Long volume;

}
