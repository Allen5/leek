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
@ConditionalOnProperty(prefix = "spring.datasource.admin", name = "enabled", havingValue = "true")
public class AdminDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "adminDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.admin")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "adminDataSource")
    @ConditionalOnBean(name = "adminDataSourceProperties")
    public DataSource dataSource(@Qualifier("adminDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "adminJdbcTemplate")
    @ConditionalOnBean(name = "adminDataSource")
    public JdbcTemplate jdbcTemplate(@Qualifier("adminDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
