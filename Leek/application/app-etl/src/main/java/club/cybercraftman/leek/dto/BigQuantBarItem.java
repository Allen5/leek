package club.cybercraftman.leek.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class BigQuantBarItem {

    /**
     * 交易日期
     * TODO: 此处待处理
     */
    private Date datetime;

    /**
     * 品种代码
     */
    private String productCode;

    /**
     * 合约代码
     */
    private String contractCode;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal settle;

    private Long volume;

    private Long openInterest;

    private BigDecimal amount;

}
