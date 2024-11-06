package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.infrastructure.database.AdminDataSourceConfig;
import club.cybecraftman.leek.repo.admin.model.ActionLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(AdminDataSourceConfig.class)
public interface IActionLogRepo extends JpaRepository<ActionLog, Long> {
}
