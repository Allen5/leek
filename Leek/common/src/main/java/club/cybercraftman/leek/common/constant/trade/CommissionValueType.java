package club.cybercraftman.leek.common.constant.trade;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 手续费收取类别
 */
@Getter
public enum CommissionValueType {

    FIXED(0, "固定值"),
    RATIO(1, "比例值"),
    ;

    CommissionValueType(final Integer type, final String  description) {
        this.type = type;
        this.description = description;
    }

    private final Integer type;

    private final String description;

    public static CommissionValueType parse(final Integer type) {
        Optional<CommissionValueType> op = Arrays.stream(CommissionValueType.values()).filter(c -> type.equals(c.getType())).findAny();
        if ( op.isPresent() ) {
            return op.get();
        }
        throw new LeekRuntimeException("不支持的值计算类别: " + type);
    }
}
