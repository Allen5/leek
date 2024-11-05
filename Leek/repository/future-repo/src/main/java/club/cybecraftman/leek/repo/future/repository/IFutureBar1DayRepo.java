package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.repo.future.model.FutureBar1Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureBar1DayRepo extends JpaRepository<FutureBar1Day, Long> {
}
