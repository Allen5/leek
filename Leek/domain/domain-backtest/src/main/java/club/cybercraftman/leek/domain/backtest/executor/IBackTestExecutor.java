package club.cybercraftman.leek.domain.backtest.executor;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.domain.backtest.BackTestParam;

public interface IBackTestExecutor {

    /**
     * 用于适配不同的交易市场和金融市场
     * @param market
     * @param financeType
     * @return
     */
    boolean isSupport(final Market market, final FinanceType financeType);

    /**
     * 回测执行器接口
     */
    void execute(BackTestParam param);

}
