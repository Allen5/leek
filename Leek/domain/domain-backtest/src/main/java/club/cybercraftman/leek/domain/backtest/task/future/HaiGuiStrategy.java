package club.cybercraftman.leek.domain.backtest.task.future;

import club.cybercraftman.leek.core.strategy.Signal;
import club.cybercraftman.leek.core.strategy.future.FutureBaseStrategy;

public class HaiGuiStrategy extends FutureBaseStrategy {

    @Override
    public Signal getSignal() {
        return null;
    }

    @Override
    protected void onOpen(Signal signal) {

    }

    @Override
    protected void onClose(Signal signal) {

    }

    @Override
    protected void onDeal() {

    }

    @Override
    protected void onCancel() {

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
