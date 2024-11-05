package club.cybecraftman.leek.repo.backtest.repository;

import club.cybecraftman.leek.repo.backtest.model.BackTestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestRecordRepo extends JpaRepository<BackTestRecord, Long> {
}
