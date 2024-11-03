package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.BackTestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBackTestOrderRepo extends JpaRepository<BackTestOrder, Long> {
}
