package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.repo.backtest.model.BackTestLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestLockRepo extends JpaRepository<BackTestLock, Long> {
}
