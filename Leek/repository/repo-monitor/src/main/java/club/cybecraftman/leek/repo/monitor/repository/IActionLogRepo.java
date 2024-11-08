package club.cybecraftman.leek.repo.monitor.repository;

import club.cybecraftman.leek.infrastructure.database.datasource.AdminDataSourceConfig;
import club.cybecraftman.leek.repo.monitor.model.AdminActionLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(AdminDataSourceConfig.class)
public interface IActionLogRepo extends JpaRepository<AdminActionLog, Long> {
}
