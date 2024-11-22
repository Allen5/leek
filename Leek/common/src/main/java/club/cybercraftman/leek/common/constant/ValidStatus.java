package club.cybercraftman.leek.common.constant;

import lombok.Getter;

@Getter
public enum ValidStatus {

    VALID(0, "有效"),
    INVALID(1, "失效"),

    ;

    ValidStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    private final Integer status;

    private final String description;

}
