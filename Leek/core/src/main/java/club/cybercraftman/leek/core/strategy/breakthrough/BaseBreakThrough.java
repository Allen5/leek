package club.cybercraftman.leek.core.strategy.breakthrough;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 趋势突破基类，封装通用计算方法。
 */
public abstract class BaseBreakThrough implements IBreakThrough {

    private Map<String, Object> params = new HashMap<>();

    /**
     * 设置参数
     * @param key
     * @param value
     */
    public BaseBreakThrough option(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 获取参数
     * @param key
     * @return
     * @param <T>
     */
    public <T> T getOption(String key) {
        if ( !params.containsKey(key) ) {
            throw new LeekRuntimeException("策略运行参数" + key + "未设置");
        }
        return (T) params.get(key);
    }

    /**
     * 清理参数
     */
    public void clearOptions() {
        params.clear();
    }

    /**
     * 计算SMA的值
     * @param prices
     * @return
     */
    BigDecimal getSMA(List<BigDecimal> prices) {
        double total = prices.stream().mapToDouble(BigDecimal::doubleValue).sum();
        return BigDecimal.valueOf(total).divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);
    }

}
