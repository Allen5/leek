package club.cybercraftman.leek.core.broker;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 经纪人
 */
@Builder
@Data
@ToString
public class Broker {

    /**
     * 初始资金
     */
    private BigDecimal initCapital;

    /**
     * 最终资金
     */
    private BigDecimal finalCapital;

    /**
     * 收益
     */
    private BigDecimal profit;

    /**
     * 净收益
     */
    private BigDecimal net;

    /**
     * 手续费，服务费
     */
    private BigDecimal commission;

}
