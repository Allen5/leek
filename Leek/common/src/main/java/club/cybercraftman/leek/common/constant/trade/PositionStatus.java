package club.cybercraftman.leek.common.constant.trade;

import lombok.Getter;

@Getter
public enum PositionStatus {

    OPEN(0, "开仓"),
    CLOSE(1, "平仓"),
    ;

    PositionStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    private final Integer status;

    private final String description;
}
