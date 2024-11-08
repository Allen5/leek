package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.infrastructure.database.datasource.FutureDataSourceConfig;
import club.cybecraftman.leek.repo.future.model.FutureExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(FutureDataSourceConfig.class)
public interface IFutureExchangeRepo extends JpaRepository<FutureExchange, Long> {
}
