package club.cybercraftman.leek.core.strategy.common;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
@Slf4j
public class StrategyBuilder {
    public BaseStrategy find(final String strategyClassName) {
        try {
            return (BaseStrategy) Class.forName(strategyClassName).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            log.info("实例化策略失败. className: {}", strategyClassName, e);
            throw new LeekRuntimeException("不支持的策略id: " + strategyClassName);
        }
    }

}
