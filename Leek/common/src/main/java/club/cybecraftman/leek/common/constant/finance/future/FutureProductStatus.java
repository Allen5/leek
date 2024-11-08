package club.cybecraftman.leek.common.constant.finance.future;

import lombok.Getter;

@Getter
public enum FutureProductStatus {

    LIST(0, "上市"),
    DE_LIST(1, "退市"),
    ;

    FutureProductStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    private final Integer status;

    private final String description;
}
