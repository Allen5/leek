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
    public boolean hasEnoughCapital(final BigDecimal price, final Integer volume, final BigDecimal multiplier, final BigDecimal priceTick) {
        BigDecimal netCost = getDepositValue(price, volume, multiplier, priceTick);
        BigDecimal totalCost = netCost.add(getCommissionValue(CommissionCategory.TRADE_FEE, getNet(price, volume, multiplier, priceTick)));
        return capital.compareTo(totalCost) > 0;
    }

    /**
     * 计算保证金（100%即为资产价值）
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getDepositValue(final BigDecimal price, final Integer volume, final BigDecimal multiplier, final BigDecimal priceTick) {
        return price.multiply(new BigDecimal(volume)).multiply(multiplier).multiply(depositRatio).multiply(priceTick);
    }

    /**
     * 获取资产净值
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getNet(final BigDecimal price, final Integer volume, final BigDecimal multiplier, final BigDecimal priceTick) {
        return price.multiply(new BigDecimal(volume)).multiply(multiplier).multiply(priceTick);
    }

    /**
     * 计算费用
     * @param category
     * @param net
     * @return
     */
    public BigDecimal getCommissionValue(final CommissionCategory category, final BigDecimal net) {
        if ( CollectionUtils.isEmpty(this.commissionMap) ) {
            return BigDecimal.ZERO;
        }
        Commission commission = this.commissionMap.getOrDefault(category, null);
        if ( null == commission ) {
            return BigDecimal.ZERO;
        }
        if ( null == net ) {
            return BigDecimal.ZERO;
        }
        BigDecimal tax = BigDecimal.ZERO;
        if (CommissionValueType.FIXED.equals(commission.getValueType())) {
            tax = tax.add(commission.getValue());
        } else if ( CommissionValueType.RATIO.equals(commission.getValueType()) ){
            tax = tax.add(net.multiply(commission.getValue()));
        }
        return tax;
    }

    /**
     * 计算手续费
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getCommission(final BigDecimal price, final Integer volume, final BigDecimal multiplier, final BigDecimal priceTick) {
        BigDecimal net = getNet(price, volume, multiplier, priceTick);
        return getCommissionValue(CommissionCategory.TRADE_FEE, net);
    }

    /**
     * 扣减金额
     * @param amount
     */
    public void subCapital(final BigDecimal amount) {
        this.capital = this.capital.subtract(amount);
    }

    /**
     * 增加金额
     * @param amount
     */
    public void addCapital(final BigDecimal amount) {
        this.capital = this.capital.add(amount);
    }

}
