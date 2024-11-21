package club.cybercraftman.leek.common.constant.creep;

import lombok.Getter;

@Getter
public enum SourceName {

    SINA("新浪期货", "新浪期货数据源"),
    CZCE("郑商所", "郑商所官方数据源"),
    DCE("大商所", "大商所官方数据源"),
    GFEX("广期所", "广期所官方数据"),
    CFFEX("中金所", "中金所官方数据"),
    SHFE("上期所", "上期所官方数据"),
    INE("上期能源", "上期能源官方数据"),

    /**
     * 缺少收盘价和当日结算价，放弃该数据源
     */
    @Deprecated
    EAST_MONEY("东方财富", "东方财富官方数据"),

    ;

    SourceName(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    private final String name;

    private final String description;
}
