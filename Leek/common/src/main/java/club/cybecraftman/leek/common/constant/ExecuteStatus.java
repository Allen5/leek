package club.cybecraftman.leek.common.constant;

import lombok.Getter;

public enum ExecuteStatus {

    EXECUTING(0, "执行中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败"),

    ;

    ExecuteStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    @Getter
    private Integer status;

    @Getter
    private String description;

}
