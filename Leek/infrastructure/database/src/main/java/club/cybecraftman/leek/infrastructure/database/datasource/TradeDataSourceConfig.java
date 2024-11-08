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
@ConditionalOnProperty(prefix = "spring.datasource.trade", name = "enabled", havingValue = "true")
public class TradeDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "tradeDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.trade")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "tradeDataSource")
    @ConditionalOnBean(name = "tradeDataSourceProperties")
    public DataSource dataSource(@Qualifier("tradeDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "tradeJdbcTemplate")
    @ConditionalOnBean(name = "tradeDataSource")
    public JdbcTemplate jdbcTemplate(@Qualifier("tradeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
