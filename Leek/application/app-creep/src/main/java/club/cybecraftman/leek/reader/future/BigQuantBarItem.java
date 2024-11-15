package club.cybecraftman.leek.reader.future;

import club.cybecraftman.leek.reader.converter.ThousandsToBigDecimalConverter;
import club.cybecraftman.leek.reader.converter.ThousandsToLongConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class BigQuantBarItem {

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
