package club.cybercraftman.leek.common.constant.trade;


import lombok.Getter;

public enum BackTestRecordStatus {

    NOT_START(0, "开始"),
    EXECUTING(1, "执行中"),
    FAIL(2, "回测失败"),
    SUCCESS(3, "回测成功")
    ;

    BackTestRecordStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    @Getter
    private Integer status;

    @Getter
    private String description;
}
