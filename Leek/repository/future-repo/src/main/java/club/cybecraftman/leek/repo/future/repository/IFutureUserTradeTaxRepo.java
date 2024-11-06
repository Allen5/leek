package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.infrastructure.database.FutureDataSourceConfig;
import club.cybecraftman.leek.repo.future.model.FutureUserTradeTax;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(FutureDataSourceConfig.class)
public interface IFutureUserTradeTaxRepo extends JpaRepository<FutureUserTradeTax, Long> {
}
