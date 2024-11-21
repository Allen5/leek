package club.cybercraftman.leek.common.constant.finance;

import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum BarType {
    TICK(100, "Tick周期"),
    MIN(200, "1分钟周期"),
    MIN_5(201, "5分钟周期"),
    DAILY(300, "日周期"),
    WEEKLY(400, "周周期"),
    MONTHLY(500, "月周期"),
    ;

    BarType(final Integer type, final String description) {
        this.type = type;
        this.description = description;
    }

    private final Integer type;

    private final String description;

    public static BarType parse(final Integer type) throws LeekException {
        if (  type == null ) {
            throw new LeekRuntimeException("行情类型不能为空");
        }
        Optional<BarType> op = Arrays.stream(BarType.values()).filter(m -> Objects.equals(m.getType(), type)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekException("不支持的行情类型: " + type);
        }
        return op.get();
    }

}
