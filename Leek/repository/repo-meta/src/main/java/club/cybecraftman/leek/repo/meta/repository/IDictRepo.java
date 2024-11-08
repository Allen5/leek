package club.cybecraftman.leek.repo.meta.repository;

import club.cybecraftman.leek.repo.meta.model.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDictRepo extends JpaRepository<Dict, String> {
}
