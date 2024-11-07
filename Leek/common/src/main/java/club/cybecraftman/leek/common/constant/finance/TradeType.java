package club.cybecraftman.leek.common.constant.finance;

import lombok.Getter;

/**
 * 方向
 */
public enum TradeType {

    OPEN(0, "开仓"),
    CLOSE(1, "平仓"),

    ;

    TradeType(final Integer type, final String description) {
        this.type = type;
        this.description = description;
    }

    @Getter
    private Integer type;

    @Getter
    private String description;

}
