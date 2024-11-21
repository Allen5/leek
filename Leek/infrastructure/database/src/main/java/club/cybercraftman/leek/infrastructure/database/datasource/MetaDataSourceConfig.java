package club.cybercraftman.leek.infrastructure.database.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MetaDataSourceConfig {

    /**
     * 数据源配置
     * @return
     */
    @Primary
    @Bean(name = "metaDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.meta")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @param dataSourceProperties
     * @return
     */
    @Primary
    @Bean(name = "metaDataSource")
    public DataSource dataSource(@Qualifier("metaDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "metaJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("metaDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
