package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.BackTestTradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestTradeLogRepo extends JpaRepository<BackTestTradeLog, Long> {
}
