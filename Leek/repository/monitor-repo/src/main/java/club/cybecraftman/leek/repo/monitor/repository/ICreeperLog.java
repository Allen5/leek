package club.cybecraftman.leek.repo.monitor.repository;

import club.cybecraftman.leek.repo.monitor.model.CreepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreeperLog extends JpaRepository<CreepLog, Long> {
}
