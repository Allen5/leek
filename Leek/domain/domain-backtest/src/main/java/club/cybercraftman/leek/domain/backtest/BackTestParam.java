package club.cybercraftman.leek.domain.backtest;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.domain.backtest.executor.BackTestRunningMode;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Data
@ToString
public class BackTestParam {

    private BackTestRunningMode mode;

    /**
     * 日期参数
     */
    private DateRange dateRange;

    /**
     * 回测数据中至少要包含多少个交易日
     */
    private Integer minBars;

    /**
     * 指定回测执行的策略
     */
    private String strategyClassName;

    /**
     * 策略参数
     */
    private Map<String, Object> strategyParams;

    /**
     * 初始资金
     */
    private BigDecimal capital;


}
