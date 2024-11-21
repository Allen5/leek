package club.cybercraftman.leek.common.event.etl.future;

import club.cybercraftman.leek.common.event.etl.BasicBarEventData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class FutureBarEventData extends BasicBarEventData {

    /**
     * 品种代码
     */
    private String productCode;

    /**
     * 合约代码
     */
    private String contractCode;

    /**
     * 结算价
     */
    private BigDecimal settle;

}
