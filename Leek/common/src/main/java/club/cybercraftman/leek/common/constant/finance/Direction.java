package club.cybercraftman.leek.common.constant.finance;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 方向
 */
@Getter
public enum Direction {

    LONG(0, "多头"),
    SHORT(1, "空头"),

    ;

    Direction(final Integer type, final String description) {
        this.type = type;
        this.description = description;
    }

    private final Integer type;

    private final String description;

    public static Direction parse(final Integer direction) {
        Optional<Direction> op = Arrays.stream(Direction.values()).filter(d -> d.getType().equals(direction)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不支持的交易方向: " + direction);
        }
        return op.get();
    }

}
