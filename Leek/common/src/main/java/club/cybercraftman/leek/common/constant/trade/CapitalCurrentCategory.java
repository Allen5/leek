package club.cybercraftman.leek.common.constant.trade;

import lombok.Getter;

/**
 * 资金流水类型
 */
@Getter
public enum CapitalCurrentCategory {
    COMMISSION(100, "手续费、服务费"),
    DEPOSIT(200, "保证金"),
    PROFIT(300, "收益"),
    NET(400, "收益(此处为净收益)"),
    ;

    CapitalCurrentCategory(final Integer category, final String description) {
        this.category = category;
        this.description = description;
    }

    private final Integer category;

    private final String description;

}
