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
        basePackages = "club.cybercraftman.leek.repo.monitor.repository",
        entityManagerFactoryRef = "monitorEntityManagerFactory",
        transactionManagerRef = "monitorTransactionManager"
)
@ConditionalOnProperty(prefix = "spring.datasource.monitor", name = "enabled", havingValue = "true")
public class MonitorJpaConfig {

    @Bean(name = "monitorJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.monitor")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "monitorEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("monitorDataSource") DataSource dataSource,
                                                                           @Qualifier("monitorJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybercraftman.leek.repo.monitor.model")
                .persistenceUnit("monitorPersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Bean(name = "monitorEntityManager")
    @ConditionalOnBean(name = "monitorEntityManagerFactory")
    public EntityManager entityManager(@Qualifier("monitorEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Bean(name = "monitorTransactionManager")
    @ConditionalOnBean(name = "monitorEntityManagerFactory")
    public PlatformTransactionManager transactionManager(@Qualifier("monitorEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
