package club.cybecraftman.leek.infrastructure.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "club.cybecraftman.leek.repo.stock.repository",
        entityManagerFactoryRef = "stockEntityManagerFactory",
        transactionManagerRef = "stockTransactionManager"
)
public class StockJpaConfig {

    // TODO: 这里有问题。 按需开启这个数据源的配置未生效！！
    @Bean(name = "stockJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.stock")
    @ConditionalOnProperty(name = "spring.jpa.stock")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "stockEntityManagerFactory")
    @ConditionalOnBean(name = {"stockDataSource", "stockJpaProperties"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("stockDataSource") DataSource dataSource,
                                                                           @Qualifier("stockJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybecraftman.leek.repo.stock.model")
                .persistenceUnit("stockPersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Bean(name = "stockEntityManager")
    @ConditionalOnBean(name = "stockEntityManagerFactory")
    public EntityManager entityManager(@Qualifier("stockEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Bean(name = "stockTransactionManager")
    @ConditionalOnBean(name = "stockEntityManagerFactory")
    public PlatformTransactionManager transactionManager(@Qualifier("stockEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
