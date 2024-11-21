package club.cybercraftman.leek.repo.meta.repository;

import club.cybercraftman.leek.repo.meta.model.CreeperConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreeperConfigRepo extends JpaRepository<CreeperConfig, Long> {
}
