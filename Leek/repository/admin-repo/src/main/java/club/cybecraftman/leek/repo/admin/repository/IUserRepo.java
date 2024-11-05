package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.repo.admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepo extends JpaRepository<User, Long> {
}
