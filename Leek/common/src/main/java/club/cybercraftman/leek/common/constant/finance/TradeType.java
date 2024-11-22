package club.cybercraftman.leek.common.constant.finance;

import lombok.Getter;

/**
 * 方向
 */
@Getter
public enum TradeType {

    OPEN(0, "开仓"),
    CLOSE(1, "平仓"),

    ;

    TradeType(final Integer type, final String description) {
        this.type = type;
        this.description = description;
    }

    private final Integer type;

    private final String description;

}
