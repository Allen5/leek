package club.cybecraftman.leek.repo.trade.repository;

import club.cybecraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybecraftman.leek.repo.trade.model.Position;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IPositionRepo extends JpaRepository<Position, Long> {
}
