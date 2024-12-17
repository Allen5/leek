package club.cybercraftman.leek.infrastructure.compute.spark;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@ToString
public class SessionParam {

    private Map<String, Object> params;

    public void add(final String key, final Object value) {
        if ( null == this.params ) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
    }

    public void clear() {
        if ( null == this.params ) {
            return ;
        }
        this.params.clear();
    }

}
