package club.cybercraftman.leek.core.broker;

import club.cybercraftman.leek.common.constant.trade.CommissionCategory;
import club.cybercraftman.leek.common.constant.trade.CommissionValueType;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 手续费、服务费
 */
@Data
@ToString
public class BrokerCommission {

    /**
     * 费用类型
     * @see CommissionCategory
     */
    private CommissionCategory category;

    /**
     * 费用计算类型
     * @see CommissionValueType
     */
    private CommissionValueType valueType;

    /**
     * 值
     */
    private BigDecimal value;

}
