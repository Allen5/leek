package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.infrastructure.database.datasource.BackTestDataSourceConfig;
import club.cybecraftman.leek.repo.backtest.model.BackTestPosition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(BackTestDataSourceConfig.class)
public interface IBackTestPositionRepo extends JpaRepository<BackTestPosition, Long> {
}
