package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StrategyBuilder {

    @Autowired
    private List<IStrategy> strategies;

    public IStrategy find(final String strategyId) {
        Optional<IStrategy> op = strategies.stream().filter(s -> s.getId().equals(strategyId)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不支持的策略id: " + strategyId);
        }
        return op.get();
    }

}
