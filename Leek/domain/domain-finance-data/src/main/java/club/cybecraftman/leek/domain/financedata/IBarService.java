package club.cybecraftman.leek.domain.financedata;

import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;

import java.util.List;

/**
 * 行情处理
 */
public interface IBarService<T> {

    /**
     * 根据isSupport获取指定的行情数据处理类
     * @param market
     * @param financeType
     * @param barType
     * @return
     */
    boolean isSupport(final Market market, final FinanceType financeType, final BarType barType);


    /**
     * 处理bars
     * @param bars
     */
    void handleBars(final List<T> bars);

}
