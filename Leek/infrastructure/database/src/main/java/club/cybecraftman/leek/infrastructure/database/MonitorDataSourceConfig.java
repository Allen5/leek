package club.cybecraftman.leek.infrastructure.database;

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
@ConditionalOnProperty(prefix = "spring.datasource.monitor", name = "enabled", havingValue = "true")
public class MonitorDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "monitorDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.monitor")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "monitorDataSource")
    @ConditionalOnBean(name = "monitorDataSourceProperties")
    public DataSource dataSource(@Qualifier("monitorDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "monitorJdbcTemplate")
    @ConditionalOnBean(name = "monitorDataSource")
    public JdbcTemplate jdbcTemplate(@Qualifier("monitorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
