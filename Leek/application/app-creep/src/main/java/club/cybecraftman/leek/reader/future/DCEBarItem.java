package club.cybecraftman.leek.reader.future;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class DCEBarItem {

    /**
     * 合约代码
     */
    @ExcelProperty("合约代码")
    private String contractCode;

    @ExcelProperty("开盘价")
    private BigDecimal open;

    @ExcelProperty("最高价")
    private BigDecimal high;

    @ExcelProperty("最低价")
    private BigDecimal low;

    @ExcelProperty("收盘价")
    private BigDecimal close;

    @ExcelProperty("成交量")
    private Long volume;

    @ExcelProperty("持仓量")
    private Long openInterest;

    @ExcelProperty("成交额")
    private BigDecimal amount;

}
