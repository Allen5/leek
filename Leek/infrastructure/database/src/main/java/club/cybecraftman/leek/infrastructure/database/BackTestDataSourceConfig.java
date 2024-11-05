package club.cybecraftman.leek.infrastructure.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BackTestDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "backTestDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.backtest")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "backTestDataSource")
    public DataSource dataSource(@Qualifier("backTestDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "backTestJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("backTestDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
