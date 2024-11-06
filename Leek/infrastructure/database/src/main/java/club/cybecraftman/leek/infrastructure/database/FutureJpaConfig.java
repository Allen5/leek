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
        basePackages = "club.cybecraftman.leek.repo.future.repository",
        entityManagerFactoryRef = "futureEntityManagerFactory",
        transactionManagerRef = "futureTransactionManager"
)
public class FutureJpaConfig {

    @Bean(name = "futureJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.future")
    @ConditionalOnProperty(prefix = "spring.datasource.future", name = "enabled", havingValue = "true")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "futureEntityManagerFactory")
    @ConditionalOnBean(name = {"futureDataSource", "futureJpaProperties"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("futureDataSource") DataSource dataSource,
                                                                           @Qualifier("futureJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybecraftman.leek.repo.future.model")
                .persistenceUnit("futurePersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Bean(name = "futureEntityManager")
    @ConditionalOnBean(name = "futureEntityManagerFactory")
    public EntityManager entityManager(@Qualifier("futureEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Bean(name = "futureTransactionManager")
    @ConditionalOnBean(name = "futureEntityManagerFactory")
    public PlatformTransactionManager transactionManager(@Qualifier("futureEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
