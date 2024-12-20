package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.core.strategy.common.Signal;
import club.cybercraftman.leek.core.strategy.common.TwoSideBaseStrategy;

public class HaiGuiStrategy extends TwoSideBaseStrategy {

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
