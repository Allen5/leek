package club.cybercraftman.leek.common.constant.trade;

import lombok.Getter;

/**
 * 策略运行参数
 */
@Getter
public enum StrategyParam {

    /**
     * 信号计算参数
     */
    PERIOD(1000, "PERIOD", "周期"),
    FAST_PERIOD(1001, "FAST_PERIOD", "快线周期"),
    SLOW_PERIOD(1002, "SLOW_PERIOD", "慢线周期"),

    BREAKTHROUGH_CLZ(1100, "BREAKTHROUGH_CLZ", "突破计算类名"),
    BREAKTHROUGH_SD_FACTOR(1101, "BREAKTHROUGH_SD_FACTOR", "标准差倍数"),

    /**
     * 风控参数
     */
    TRADE_CASH_RATE(2000, "TRADE_CASH_RATE", "交易资金比例（每笔）"),
    STOP_LOSS_CLZ(2001, "STOP_LOSS_CLZ", "止损计算类名"),
    STOP_LOSS_RATE(2002, "STOP_LOSS_RATE", "止损比例(每笔)"),

    /**
     * 交易参数
     */
    DIRECTION_LIMIT(3000, "DIRECTION_LIMIT", "交易方向限制-无限制、多、空"),
    INCREASE_POSITION_LIMIT_COUNT(3001, "INCREASE_POSITION_LIMIT_COUNT", "增仓限制次数"),
    FORCE_CLOSE_TRIGGER_COUNT(3002, "FORCE_CLOSE_INTERVAL", "在临近交易日的前N天进行持仓强平操作"),
    ;

    StrategyParam(final Integer category, final String key, final String description) {
        this.category = category;
        this.key = key;
        this.description = description;
    }

    private final Integer category;

    private final String key;

    private final String description;

}
