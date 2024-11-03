package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.Menu;
import club.cybecraftman.leek.repo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMenuRepo extends JpaRepository<Menu, Long> {
}
