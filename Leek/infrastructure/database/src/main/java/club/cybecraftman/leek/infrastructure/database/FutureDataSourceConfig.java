package club.cybecraftman.leek.infrastructure.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class FutureDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Bean(name = "futureDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.future")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Bean(name = "futureDataSource")
    public DataSource dataSource(@Qualifier("futureDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "futureJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("futureDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
