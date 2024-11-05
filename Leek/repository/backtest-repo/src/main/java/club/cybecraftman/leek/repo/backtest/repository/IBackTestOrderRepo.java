package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.repo.backtest.model.BackTestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestOrderRepo extends JpaRepository<BackTestOrder, Long> {
}
