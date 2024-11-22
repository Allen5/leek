package club.cybercraftman.leek.common.constant.trade;


import lombok.Getter;

@Getter
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

    private final Integer status;

    private final String description;
}
