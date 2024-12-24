package club.cybercraftman.leek.common.constant.trade;

import lombok.Getter;

/**
 * 资金流水类型: 收入｜支出
 */
@Getter
public enum CapitalCurrentType {
    INCOME(1, "收入"),
    OUTCOME(2, "支出"),
    ;

    CapitalCurrentType(final Integer type, final String description) {
        this.type = type;
        this.description = description;
    }

    private final Integer type;

    private final String description;
}
