package club.cybercraftman.leek.common.constant.finance;

import lombok.Getter;

/**
 * 订单状态
 */
@Getter
public enum OrderStatus {

    ORDER(0, "下单"),
    REVOKE(1, "撤单"),
    DEAL(2, "成交"),
    FAIL(3, "失败"),
    ;

    OrderStatus(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }

    private final Integer status;

    private final String description;
}
