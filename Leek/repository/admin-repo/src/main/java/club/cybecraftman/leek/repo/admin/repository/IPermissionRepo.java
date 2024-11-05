package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.repo.admin.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermissionRepo extends JpaRepository<Permission, Long> {
}
