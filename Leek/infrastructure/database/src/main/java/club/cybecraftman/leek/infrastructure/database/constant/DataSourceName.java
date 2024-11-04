package club.cybecraftman.leek.infrastructure.database.constant;

import lombok.Getter;

/**
 * 数据源名称
 */
public enum DataSourceName {
    ADMIN("admin", "管理台"),
    FUTURE("future", "期货信息库"),
    STOCK("stock", "股票信息库"),
    BACKTEST("backtest", "回测库"),
    ;

    DataSourceName(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Getter
    private String name;

    @Getter
    private String description;

}
