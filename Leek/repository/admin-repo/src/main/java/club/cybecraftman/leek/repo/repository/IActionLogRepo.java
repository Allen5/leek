package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.ActionLog;
import club.cybecraftman.leek.repo.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IActionLogRepo extends JpaRepository<ActionLog, Long> {
}
