package club.cybercraftman.leek.core.broker;

import club.cybercraftman.leek.common.constant.ValidStatus;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.CommissionCategory;
import club.cybercraftman.leek.common.constant.trade.CommissionValueType;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.repo.trade.model.Commission;
import club.cybercraftman.leek.repo.trade.repository.ICommissionRepo;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 经纪人
 */
@Data
@ToString
public class Broker {

    private Market market;

    private FinanceType financeType;

    /**
     * 初始资金
     */
    private BigDecimal capital;

    /**
     * 保证金比例. 默认100%. 可设置
     */
    private BigDecimal depositRatio;

    /**
     * 手续费、服务费等
     */
    private Map<CommissionCategory, BrokerCommission> commissionMap;

    public Broker(final Market market, final FinanceType financeType, final BigDecimal capital, final BigDecimal depositRatio) {
        this.market = market;
        this.financeType = financeType;
        this.capital = capital;
        this.depositRatio = depositRatio;
    }

    @PostConstruct
    public void initCommission() {
        ICommissionRepo repo = SpringContextUtil.getBean(ICommissionRepo.class);
        List<Commission> commissions = repo.findAllByStatus(market.getCode(), financeType.getType(), ValidStatus.VALID.getStatus());
        if ( CollectionUtils.isEmpty(commissions) ) {
            return;
        }
        this.commissionMap = commissions.stream().map(c -> {
            BrokerCommission brokerCommission = new BrokerCommission();
            brokerCommission.setCategory(CommissionCategory.parse(c.getCategory()));
            brokerCommission.setValueType(CommissionValueType.parse(c.getType()));
            brokerCommission.setValue(c.getCommission());
            return brokerCommission;
        }).collect(Collectors.toMap(BrokerCommission::getCategory, c -> c));
    }

    /**
     * 判断是否有足够的资金开单
     */
    public boolean hasEnoughCapital(final BigDecimal price, final Long volume, final BigDecimal multiplier, final BigDecimal priceTick) {
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
    public BigDecimal getDepositValue(final BigDecimal price, final Long volume, final BigDecimal multiplier, final BigDecimal priceTick) {
        return price.multiply(new BigDecimal(volume)).multiply(multiplier).multiply(depositRatio).multiply(priceTick);
    }

    /**
     * 获取资产净值
     * @param price
     * @param volume
     * @param multiplier
     * @return
     */
    public BigDecimal getNet(final BigDecimal price, final Long volume, final BigDecimal multiplier, final BigDecimal priceTick) {
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
        BrokerCommission brokerCommission = this.commissionMap.getOrDefault(category, null);
        if ( null == brokerCommission) {
            return BigDecimal.ZERO;
        }
        if ( null == net ) {
            return BigDecimal.ZERO;
        }
        BigDecimal tax = BigDecimal.ZERO;
        if (CommissionValueType.FIXED.equals(brokerCommission.getValueType())) {
            tax = tax.add(brokerCommission.getValue());
        } else if ( CommissionValueType.RATIO.equals(brokerCommission.getValueType()) ){
            tax = tax.add(net.multiply(brokerCommission.getValue()));
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
    public BigDecimal getCommission(final BigDecimal price, final Long volume, final BigDecimal multiplier, final BigDecimal priceTick) {
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
