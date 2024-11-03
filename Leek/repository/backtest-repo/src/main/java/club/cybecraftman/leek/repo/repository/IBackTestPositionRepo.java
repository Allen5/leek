package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.BackTestPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestPositionRepo extends JpaRepository<BackTestPosition, Long> {
}
