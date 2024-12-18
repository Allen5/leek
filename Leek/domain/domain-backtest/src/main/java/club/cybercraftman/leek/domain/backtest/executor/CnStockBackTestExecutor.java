package club.cybercraftman.leek.domain.backtest.executor;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.task.BackTestTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CnStockBackTestExecutor extends BaseBackTestExecutor {

    @Override
    public boolean isSupport(Market market, FinanceType financeType) {
        boolean result = Market.CN.equals(market) && FinanceType.STOCK.equals(financeType);
        if ( result ) {
            this.setMarket(market);
            this.setFinanceType(financeType);
        }
        return result;
    }

    @Override
    protected List<String> loadCodes(Integer minBars) {
        return List.of();
    }

    @Override
    protected BackTestTask createTask(String code, int startPercent, int endPercent, BackTestParam param) {
        return null;
    }

}
