package club.cybecraftman.leek.common.constant.admin;

import lombok.Getter;

@Getter
public enum UserStatus {

    NORMAL(0, "正常"),
    FORBIDDEN(1, "封禁"),
    ;

    UserStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    private final Integer status;

    private final String description;
}
