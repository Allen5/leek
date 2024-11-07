package club.cybecraftman.leek.common.constant;

import lombok.Getter;

public enum ValidStatus {

    VALID(0, "有效"),
    INVALID(1, "失效"),

    ;

    ValidStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    @Getter
    private Integer status;

    @Getter
    private String description;

}
