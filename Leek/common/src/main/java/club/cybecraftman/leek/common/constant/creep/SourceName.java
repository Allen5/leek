package club.cybecraftman.leek.common.constant.creep;

import lombok.Getter;

public enum SourceName {

    SINA("新浪期货", "新浪期货数据源"),
    CZCE("郑商所", "郑商所官方数据源"),

    ;

    SourceName(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Getter
    private String name;

    @Getter
    private String description;
}
