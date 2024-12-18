package club.cybercraftman.leek;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.executor.BackTestExecutorBuilder;
import club.cybercraftman.leek.domain.backtest.executor.BackTestRunningMode;
import club.cybercraftman.leek.domain.backtest.executor.IBackTestExecutor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BackTestApplication.class)
@Slf4j
public class TestFutureBackTest {

    @Autowired
    private BackTestExecutorBuilder builder;

    /**
     * 单品种测试
     */
    @Test
    public void testSingleProduct() {
        IBackTestExecutor executor = builder.find(Market.CN, FinanceType.FUTURE);
        BackTestParam param = BackTestParam.builder().mode(BackTestRunningMode.DEV).build();
        executor.execute(param);
    }

}
