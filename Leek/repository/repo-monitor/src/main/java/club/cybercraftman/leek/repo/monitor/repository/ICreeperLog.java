package club.cybercraftman.leek.repo.monitor.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.MonitorDataSourceConfig;
import club.cybercraftman.leek.repo.monitor.model.CreepLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(MonitorDataSourceConfig.class)
public interface ICreeperLog extends JpaRepository<CreepLog, Long> {
}
