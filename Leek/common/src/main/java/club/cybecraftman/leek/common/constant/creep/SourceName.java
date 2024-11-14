package club.cybecraftman.leek.common.constant.creep;

import lombok.Getter;

@Getter
public enum SourceName {

    SINA("新浪期货", "新浪期货数据源"),
    CZCE("郑商所", "郑商所官方数据源"),
    DCE("大商所", "大商所官方数据源"),

    ;

    SourceName(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    private final String name;

    private final String description;
}
