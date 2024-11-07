package club.cybecraftman.leek.core.strategy;

import club.cybecraftman.leek.core.strategy.dto.Bar;

import java.util.List;
import java.util.Map;

public interface IStrategy {

    /**
     * 下一个bar数据到达，触发事件
     */
    default void onNextBar(Bar bar) {}

    /**
     * 多交易代码触发
     * @param bars
     */
    default void onNextBar(List<Bar> bars) {}

    /**
     * 多交易代码触发
     * @param bars
     */
    default void onNextBar(Map<String, List<Bar>> bars) {}

}
