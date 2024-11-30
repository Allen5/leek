package club.cybercraftman.leek.dto;

import club.cybercraftman.leek.dto.converter.DatetimeConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class BigQuantBarItem {

    /**
     * 交易日期
     */
    @ExcelProperty(value = "date", converter = DatetimeConverter.class)
    private Date datetime;

    /**
     * 品种代码
     */
    @ExcelProperty("product_code")
    private String productCode;

    /**
     * 合约代码
     */
    @ExcelProperty("instrument")
    private String contractCode;

    @ExcelProperty("open")
    private BigDecimal open;

    @ExcelProperty("high")
    private BigDecimal high;

    @ExcelProperty("low")
    private BigDecimal low;

    @ExcelProperty("close")
    private BigDecimal close;

    @ExcelProperty("settle")
    private BigDecimal settle;

    @ExcelProperty("volume")
    private Long volume;

    @ExcelProperty("open_interest")
    private Long openInterest;

    @ExcelProperty("amount")
    private BigDecimal amount;

}
