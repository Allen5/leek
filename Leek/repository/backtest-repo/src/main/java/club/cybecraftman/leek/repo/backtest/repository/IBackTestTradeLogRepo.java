package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.repo.backtest.model.BackTestTradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestTradeLogRepo extends JpaRepository<BackTestTradeLog, Long> {
}
