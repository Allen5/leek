package club.cybecraftman.leek.infrastructure.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class StockDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "stockDataSourceConfig")
    @ConfigurationProperties(prefix = "spring.datasource.stock")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "stockDataSource")
    public DataSource dataSource(@Qualifier("stockDataSourceConfig") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "stockJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("stockDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
