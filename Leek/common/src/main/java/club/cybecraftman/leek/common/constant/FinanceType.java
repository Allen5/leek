package club.cybecraftman.leek.common.constant;

import lombok.Getter;

public enum FinanceType {

    FUTURE("future", "期货市场"),
    STOCK("stock", "股票市场"),

    ;

    FinanceType(final String type, final String description) {
        this.type = type;
        this.description = description;
    }

    @Getter
    private String type;

    @Getter
    private String description;
}
