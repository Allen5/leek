package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.BackTestLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestLockRepo extends JpaRepository<BackTestLock, Long> {
}
