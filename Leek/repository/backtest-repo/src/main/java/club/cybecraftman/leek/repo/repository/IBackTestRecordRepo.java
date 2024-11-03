package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.BackTestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestRecordRepo extends JpaRepository<BackTestRecord, Long> {
}
