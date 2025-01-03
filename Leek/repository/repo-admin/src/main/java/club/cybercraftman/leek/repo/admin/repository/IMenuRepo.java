package club.cybercraftman.leek.repo.admin.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.AdminDataSourceConfig;
import club.cybercraftman.leek.repo.admin.model.Menu;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(AdminDataSourceConfig.class)
public interface IMenuRepo extends JpaRepository<Menu, Long> {
}
