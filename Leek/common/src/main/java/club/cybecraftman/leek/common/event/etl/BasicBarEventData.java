package club.cybecraftman.leek.common.event.etl;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class BasicBarEventData {

    private Date datetime;

    /**
     * 交易代码
     */
    private String symbol;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private Long volume;

    private Long openInterest;

    private BigDecimal amount;

}
