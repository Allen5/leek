package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.Role;
import club.cybecraftman.leek.repo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepo extends JpaRepository<Role, Long> {
}
