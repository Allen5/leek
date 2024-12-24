package club.cybercraftman.leek.core.broker;

import club.cybercraftman.leek.common.constant.trade.CommissionCategory;
import club.cybercraftman.leek.common.constant.trade.CommissionValueType;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.core.service.BackTestCapitalCurrentService;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
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
    public boolean hasEnoughCapital(final BigDecimal price, final Integer volume, final BigDecimal multiplier) {
        BigDecimal netCost = getDepositValue(price, volume, multiplier);
        BigDecimal totalCost = netCost.add(getCommissionValue(CommissionCategory.TRADE_FEE, getNet(price, volume, multiplier)));
        return capital.compareTo(totalCost) > 0;
    }

    /**
     * 计算保证金（100%即为资产价值）
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getDepositValue(final BigDecimal price, final Integer volume, final BigDecimal multiplier) {
        return price.multiply(new BigDecimal(volume)).multiply(multiplier).multiply(depositRatio);
    }

    /**
     * 获取资产净值
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getNet(final BigDecimal price, final Integer volume, final BigDecimal multiplier) {
        return price.multiply(new BigDecimal(volume)).multiply(multiplier);
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
     * 扣减保证金
     * @param price
     * @param volume
     */
    public void subDepositValue(final BigDecimal price, final Integer volume, final BigDecimal multiplier) {
        BigDecimal cost = getDepositValue(price, volume, multiplier);
        this.capital = this.capital.subtract(cost);
    }

    /**
     * 退回保证金
     * @param price
     * @param volume
     */
    public void addDepositValue(final BigDecimal price, final Integer volume, final BigDecimal multiplier) {
        BigDecimal cost = getDepositValue(price, volume, multiplier);
        this.capital = this.capital.add(cost);
    }

    /**
     * 计算手续费
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getCommission(final BigDecimal price, final Integer volume, final BigDecimal multiplier) {
        BigDecimal net = getNet(price, volume, multiplier);
        return getCommissionValue(CommissionCategory.TRADE_FEE, net);
    }

    /**
     * 扣除手续费
     */
    @Transactional
    public void subCommission(final Long recordId,
                              final Date currentDatetime,
                              final BigDecimal price,
                              final Integer volume,
                              final BigDecimal multiplier) {
        BigDecimal commission = getCommission(price, volume, multiplier);
        BackTestCapitalCurrentService service = SpringContextUtil.getBean(BackTestCapitalCurrentService.class);
        service.subCommission(recordId, currentDatetime, commission);
        this.capital = this.capital.subtract(commission);
    }

    /**
     * 增加收益
     *
     * @param net
     */
    public void addNet(final BigDecimal net) {
        this.capital = this.capital.add(net);
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
