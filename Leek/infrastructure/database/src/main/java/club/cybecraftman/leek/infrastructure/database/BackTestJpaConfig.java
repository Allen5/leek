package club.cybecraftman.leek.infrastructure.database;

import org.springframework.beans.factory.annotation.Qualifier;
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
        basePackages = "club.cybecraftman.leek.repo.backtest.repository",
        entityManagerFactoryRef = "backTestEntityManagerFactory",
        transactionManagerRef = "backTestTransactionManager"
)
public class BackTestJpaConfig {

    @Bean(name = "backTestJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.backtest")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "backTestEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("backTestDataSource") DataSource dataSource,
                                                                           @Qualifier("backTestJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybecraftman.leek.repo.backtest.model")
                .persistenceUnit("backTestPersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Bean(name = "backTestEntityManager")
    public EntityManager entityManager(@Qualifier("backTestEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Bean(name = "backTestTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("backTestEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
