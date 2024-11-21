package club.cybercraftman.leek.infrastructure.database.datasource;

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
        basePackages = "club.cybercraftman.leek.repo.trade.repository",
        entityManagerFactoryRef = "tradeEntityManagerFactory",
        transactionManagerRef = "tradeTransactionManager"
)
@ConditionalOnProperty(prefix = "spring.datasource.trade", name = "enabled", havingValue = "true")
public class TradeJpaConfig {

    @Bean(name = "tradeJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.trade")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "tradeEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("tradeDataSource") DataSource dataSource,
                                                                           @Qualifier("tradeJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybercraftman.leek.repo.trade.model")
                .persistenceUnit("tradePersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Bean(name = "tradeEntityManager")
    @ConditionalOnBean(name = "tradeEntityManagerFactory")
    public EntityManager entityManager(@Qualifier("tradeEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Bean(name = "tradeTransactionManager")
    @ConditionalOnBean(name = "tradeEntityManagerFactory")
    public PlatformTransactionManager transactionManager(@Qualifier("tradeEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
