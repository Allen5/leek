package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.repo.backtest.model.BackTestPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestPositionRepo extends JpaRepository<BackTestPosition, Long> {
}
