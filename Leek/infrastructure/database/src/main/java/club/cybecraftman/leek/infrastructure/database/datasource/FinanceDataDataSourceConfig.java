package club.cybecraftman.leek.infrastructure.database.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.finance-data", name = "enabled", havingValue = "true")
public class FinanceDataDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "financeDataDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.finance-data")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "financeDataDataSource")
    @ConditionalOnBean(name = "financeDataDataSourceProperties")
    public DataSource dataSource(@Qualifier("financeDataDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "financeDataJdbcTemplate")
    @ConditionalOnBean(name = "financeDataDataSource")
    public JdbcTemplate jdbcTemplate(@Qualifier("financeDataDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
