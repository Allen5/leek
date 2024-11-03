package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.Menu;
import club.cybecraftman.leek.repo.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermissionRepo extends JpaRepository<Permission, Long> {
}
