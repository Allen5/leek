package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.repo.admin.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepo extends JpaRepository<Role, Long> {
}
