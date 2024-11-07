package club.cybecraftman.leek.common.constant;

import lombok.Getter;

@Getter
public enum ExecuteStatus {

    EXECUTING(0, "执行中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败"),

    ;

    ExecuteStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    private final Integer status;

    private final String description;

}
