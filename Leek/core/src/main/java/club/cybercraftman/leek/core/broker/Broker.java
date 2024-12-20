package club.cybercraftman.leek.core.broker;

import club.cybercraftman.leek.common.constant.trade.CommissionCategory;
import club.cybercraftman.leek.common.constant.trade.CommissionValueType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Map;

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
    private BigDecimal capital;

    /**
     * 保证金比例. 默认100%. 可设置
     */
    @Builder.Default()
    private BigDecimal depositRatio = new BigDecimal("100.0");

    /**
     * 手续费、服务费等
     */
    private Map<CommissionCategory, Commission> commissionMap;

    /**
     * 判断是否有足够的资金开单
     */
    public boolean hasEnoughCapital(final BigDecimal price, final Integer volume) {
        BigDecimal netCost = getNetCost(price, volume);
        BigDecimal totalCost = netCost.add(getFee(CommissionCategory.TRADE_FEE, netCost));
        return capital.compareTo(totalCost) > 0;
    }

    public BigDecimal getNetCost(final BigDecimal price, final Integer volume) {
        return price.multiply(new BigDecimal(volume)).multiply(depositRatio);
    }

    /**
     * 计算费用
     * @param category
     * @param cost
     * @return
     */
    public BigDecimal getFee(final CommissionCategory category, final BigDecimal cost) {
        if ( CollectionUtils.isEmpty(this.commissionMap) ) {
            return BigDecimal.ZERO;
        }
        Commission commission = this.commissionMap.getOrDefault(category, null);
        if ( null == commission ) {
            return BigDecimal.ZERO;
        }
        if ( null == cost ) {
            return BigDecimal.ZERO;
        }
        BigDecimal tax = BigDecimal.ZERO;
        if (CommissionValueType.FIXED.equals(commission.getValueType())) {
            tax = tax.add(commission.getValue());
        } else if ( CommissionValueType.RATIO.equals(commission.getValueType()) ){
            tax = tax.add(cost.multiply(commission.getValue()));
        }
        return tax;
    }

    /**
     * 扣减金额
     * @param price
     * @param volume
     */
    public void subNetCost(final BigDecimal price, final Integer volume) {
        BigDecimal cost = getNetCost(price, volume);
        this.capital = this.capital.subtract(cost);
    }

    /**
     * 增加金额
     * @param price
     * @param volume
     */
    public void addNetCost(final BigDecimal price, final Integer volume) {
        BigDecimal cost = getNetCost(price, volume);
        this.capital = this.capital.add(cost);
    }

    /**
     * 扣除手续费
     */
    public void subCommission(final BigDecimal commission) {
        this.capital = this.capital.subtract(commission);
    }



}
