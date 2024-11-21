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
        basePackages = "club.cybercraftman.leek.repo.admin.repository",
        entityManagerFactoryRef = "adminEntityManagerFactory",
        transactionManagerRef = "adminTransactionManager"
)
@ConditionalOnProperty(prefix = "spring.datasource.admin", name = "enabled", havingValue = "true")
public class AdminJpaConfig {

    @Bean(name = "adminJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.admin")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "adminEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("adminDataSource") DataSource dataSource,
                                                                           @Qualifier("adminJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybercraftman.leek.repo.admin.model")
                .persistenceUnit("adminPersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Bean(name = "adminEntityManager")
    public EntityManager entityManager(@Qualifier("adminEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Bean(name = "adminTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("adminEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
