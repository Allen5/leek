package club.cybercraftman.leek.core.strategy;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class StrategyBuilder {

    private Map<String, BaseStrategy> cache;

    @PostConstruct
    public void init() {
        this.cache = new HashMap<>();
    }

    public BaseStrategy find(final String strategyClassName) {
        if ( cache.containsKey(strategyClassName) ) {
            return cache.get(strategyClassName);
        }
        try {
            BaseStrategy strategy = (BaseStrategy) Class.forName(strategyClassName).getDeclaredConstructor().newInstance();
            cache.put(strategyClassName, strategy);
            return strategy;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            log.info("实例化策略失败. className: {}", strategyClassName, e);
            throw new LeekRuntimeException("不支持的策略id: " + strategyClassName);
        }
    }

}
