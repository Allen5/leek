package club.cybercraftman.leek.domain.backtest.executor;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BackTestExecutorBuilder {

    @Autowired
    private List<IBackTestExecutor> executors;

    public IBackTestExecutor find(final Market market, final FinanceType financeType) {
        Optional<IBackTestExecutor> op = executors.stream().filter(e -> e.isSupport(market, financeType)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不支持回测的交易市场: " + market + "和金融市场: " + financeType);
        }
        return op.get();
    }

}
