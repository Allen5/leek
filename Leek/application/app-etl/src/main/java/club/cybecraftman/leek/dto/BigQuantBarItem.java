package club.cybecraftman.leek.dto;

import club.cybecraftman.leek.core.utils.excel.converter.DateConverter;
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
    @ExcelProperty(value = "date", converter = DateConverter.class)
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

    @ExcelProperty(value = "open")
    private BigDecimal open;

    @ExcelProperty(value = "high")
    private BigDecimal high;

    @ExcelProperty(value = "low")
    private BigDecimal low;

    @ExcelProperty(value = "close")
    private BigDecimal close;

    @ExcelProperty(value = "settle")
    private BigDecimal settle;

    @ExcelProperty(value = "volume")
    private Long volume;

    @ExcelProperty(value = "open_interest")
    private Long openInterest;

    @ExcelProperty(value = "amount")
    private BigDecimal amount;

}
