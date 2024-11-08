package club.cybecraftman.leek.infrastructure.database.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "club.cybecraftman.leek.repo.meta.repository",
        entityManagerFactoryRef = "metaEntityManagerFactory",
        transactionManagerRef = "metaTransactionManager"
)
public class MetaJpaConfig {

    @Primary
    @Bean(name = "metaJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.meta")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Primary
    @Bean(name = "metaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("metaDataSource") DataSource dataSource,
                                                                           @Qualifier("metaJpaProperties") JpaProperties jpaProperties,
                                                                           EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("club.cybecraftman.leek.repo.meta.model")
                .persistenceUnit("metaPersistenceUnit").build();
    }

    /**
     * 实体管理器
     * @param factory
     * @return
     */
    @Primary
    @Bean(name = "metaEntityManager")
    public EntityManager entityManager(@Qualifier("metaEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * 事务管理器
     * @param factory
     * @return
     */
    @Primary
    @Bean(name = "metaTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("metaEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
