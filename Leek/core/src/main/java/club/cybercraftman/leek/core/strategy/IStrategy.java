package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.core.strategy.dto.Bar;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IStrategy {

    /**
     * 初始化策略
     */
    void init(final StrategyContext context);

    /**
     * 下一个Bar的时间
     */
    Signal next(Date datetime);

    /**
     * 处理挂单
     * @param datetime
     */
    void onOrder(final Date datetime);

    /**
     * 触发交易事件
     */
    void onTrade(Signal signal);

    String getId();

    String getName();

}
