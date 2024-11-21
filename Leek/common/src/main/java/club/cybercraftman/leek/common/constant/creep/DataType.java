package club.cybercraftman.leek.common.constant.creep;

import lombok.Getter;

public enum DataType {

    FUTURE_PRODUCT("product", "品种信息"),
    FUTURE_CONTRACT("contract", "合约信息爬取"),


    BAR("bar", "日行情信息爬取"),

    // Tips: 待扩展
    ;

    DataType(final String type, final String description) {
        this.type = type;
        this.description = description;
    }

    @Getter
    private String type;

    @Getter
    private String description;
}
