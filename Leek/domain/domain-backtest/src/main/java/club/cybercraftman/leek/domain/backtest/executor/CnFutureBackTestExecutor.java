package club.cybercraftman.leek.domain.backtest.executor;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.task.BackTestTask;
import club.cybercraftman.leek.domain.backtest.task.future.FutureBackTestTask;
import club.cybercraftman.leek.repo.financedata.model.Calendar;
import club.cybercraftman.leek.repo.financedata.repository.IFutureBackTestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CnFutureBackTestExecutor extends BaseBackTestExecutor {

    @Autowired
    private IFutureBackTestRepo backTestRepo;

    @Override
    public boolean isSupport(Market market, FinanceType financeType) {
        boolean result = Market.CN.equals(market) && FinanceType.FUTURE.equals(financeType);
        if ( result ) {
            this.setMarket(market);
            this.setFinanceType(financeType);
        }
        return result;
    }


    @Override
    protected List<String> loadCodes(Integer minBars) {
        return backTestRepo.findProductCodesLargeThan(minBars);
    }

    @Override
    protected BackTestTask createTask(String code, int startPercent, int endPercent, BackTestParam param) {
        List<Date> days = getCalendars().stream().map(Calendar::getDate).collect(Collectors.toList());
        return new FutureBackTestTask(days, param.getStrategy(), code, startPercent, endPercent);
    }


}
