package club.cybecraftman.leek.repo.trade.repository.backtest;

import club.cybecraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybecraftman.leek.repo.trade.model.backtest.BackTestPosition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestPositionRepo extends JpaRepository<BackTestPosition, Long> {
}
