package club.cybercraftman.leek.core.strategy;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;

/**
 * 策略上下文
 */
@Builder
@Data
@ToString
public class StrategyContext {

    private String recordId;

    private HashMap<String, Object> params;

    public void add(final String key, final String value) {
        if ( CollectionUtils.isEmpty(this.params) ) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
    }

}
