package club.cybecraftman.leek.reader.future;

import club.cybecraftman.leek.reader.converter.ThousandsToBigDecimalConverter;
import club.cybecraftman.leek.reader.converter.ThousandsToLongConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class CZCEBarItem {

    /**
     * 合约代码
     */
    @ExcelProperty("合约代码")
    private String contractCode;

    @ExcelProperty(value = "今开盘", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal open;

    @ExcelProperty(value = "最高价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal high;

    @ExcelProperty(value = "最低价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal low;

    @ExcelProperty(value = "今收盘", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal close;

    @ExcelProperty(value = "今结算", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal settle;

    @ExcelProperty(value = "成交量(手)", converter = ThousandsToLongConverter.class)
    private Long volume;

    @ExcelProperty(value = "持仓量", converter = ThousandsToLongConverter.class)
    private Long openInterest;

    @ExcelProperty(value = "成交额(万元)", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal amount;

}
