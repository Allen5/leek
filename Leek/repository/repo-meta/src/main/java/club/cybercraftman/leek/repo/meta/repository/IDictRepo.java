package club.cybercraftman.leek.repo.meta.repository;

import club.cybercraftman.leek.repo.meta.model.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDictRepo extends JpaRepository<Dict, String> {
}
