package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.infrastructure.database.datasource.AdminDataSourceConfig;
import club.cybecraftman.leek.repo.admin.model.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(AdminDataSourceConfig.class)
public interface IUserRepo extends JpaRepository<User, Long> {
}
