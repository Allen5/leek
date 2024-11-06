package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.infrastructure.database.BackTestDataSourceConfig;
import club.cybecraftman.leek.repo.backtest.model.BackTestOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(BackTestDataSourceConfig.class)
public interface IBackTestOrderRepo extends JpaRepository<BackTestOrder, Long> {
}
