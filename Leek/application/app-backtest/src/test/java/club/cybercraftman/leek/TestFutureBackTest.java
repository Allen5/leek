package club.cybercraftman.leek;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.StrategyParam;
import club.cybercraftman.leek.domain.backtest.BackTestParam;
import club.cybercraftman.leek.domain.backtest.executor.BackTestExecutorBuilder;
import club.cybercraftman.leek.domain.backtest.executor.BackTestRunningMode;
import club.cybercraftman.leek.domain.backtest.executor.IBackTestExecutor;
import club.cybercraftman.leek.core.strategy.HaiGuiStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = BackTestApplication.class)
@Slf4j
public class TestFutureBackTest {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private BackTestExecutorBuilder builder;

    /**
     * 单品种测试
     */
    @Test
    public void testSingleProduct() throws ParseException {
        IBackTestExecutor executor = builder.find(Market.CN, FinanceType.FUTURE);
        BackTestParam param = BackTestParam.builder()
                .mode(BackTestRunningMode.DEV)
                .minBars(1000) // 对至少1000个bar的历史数据进行回测
                .capital(new BigDecimal("50000")) // 5万资金进行回测
                .strategyClassName(HaiGuiStrategy.class.getName())
                .strategyParams(buildParams())
                .dateRange(new DateRange(sdf.parse("2015-01-01"), sdf.parse("2024-12-31")))
                .build();
        executor.execute(param);
    }

    private Map<String, Object> buildParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(StrategyParam.PERIOD.getKey(), 20); // 20个bar的周期
        params.put(StrategyParam.BREAKTHROUGH_CLZ.getKey(), "club.cybercraftman.leek.core.strategy.breakthrough.BollingBreakThrough");
        params.put(StrategyParam.BREAKTHROUGH_SD_FACTOR.getKey(), new BigDecimal("2")); // 2倍标准差
        params.put(StrategyParam.TRADE_CASH_RATE.getKey(), new BigDecimal("0.2")); // 每个交易单位为总资金的20%
        params.put(StrategyParam.STOP_LOSS_RATE.getKey(), new BigDecimal("0.2")); // 20%止损
        params.put(StrategyParam.INCREASE_POSITION_LIMIT_COUNT.getKey(), 4); // 最多4次增仓
        params.put(StrategyParam.FORCE_CLOSE_TRIGGER_COUNT.getKey(), 5);
        return params;
    }

}
