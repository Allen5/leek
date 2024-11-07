package club.cybecraftman.leek.common.constant.creep;

import lombok.Getter;

public enum SourceName {

    SINA("新浪财经", "新浪数据源"),

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
