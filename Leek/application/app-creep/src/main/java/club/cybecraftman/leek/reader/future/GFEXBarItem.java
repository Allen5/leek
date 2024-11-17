package club.cybecraftman.leek.reader.future;

import club.cybecraftman.leek.core.utils.excel.converter.ThousandsToBigDecimalConverter;
import club.cybecraftman.leek.core.utils.excel.converter.ThousandsToLongConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class GFEXBarItem {

    @ExcelProperty("商品名称")
    private String productName;

    @ExcelProperty("交割月份")
    private String deliveryMonth;

    @ExcelProperty(value = "开盘价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal open;

    @ExcelProperty(value = "最高价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal high;

    @ExcelProperty(value = "最低价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal low;

    @ExcelProperty(value = "收盘价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal close;

    @ExcelProperty(value = "结算价", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal settle;

    @ExcelProperty(value = "成交量", converter = ThousandsToLongConverter.class)
    private Long volume;

    @ExcelProperty(value = "持仓量", converter = ThousandsToLongConverter.class)
    private Long openInterest;

    @ExcelProperty(value = "成交额", converter = ThousandsToBigDecimalConverter.class)
    private BigDecimal amount;
}
