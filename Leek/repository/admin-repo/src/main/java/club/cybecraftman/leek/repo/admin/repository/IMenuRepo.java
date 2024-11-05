package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.repo.admin.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMenuRepo extends JpaRepository<Menu, Long> {
}
