package club.cybecraftman.leek.common.constant.finance;

import lombok.Getter;

/**
 * 方向
 */
public enum Direction {

    LONG(0, "多头"),
    SHORT(1, "空头"),

    ;

    Direction(final Integer type, final String description) {
        this.type = type;
        this.description = description;
    }

    @Getter
    private Integer type;

    @Getter
    private String description;

}
