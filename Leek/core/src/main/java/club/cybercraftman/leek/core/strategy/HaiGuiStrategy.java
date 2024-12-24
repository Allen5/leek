package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.core.strategy.common.BaseStrategy;
import club.cybercraftman.leek.core.strategy.common.Signal;

public class HaiGuiStrategy extends BaseStrategy {

    @Override
    public Signal getSignal() {
        // TODO: 计算信号
        return null;
    }

    @Override
    public String getId() {
        return "HaiGui";
    }

    @Override
    public String getName() {
        return "海龟策略";
    }
}
