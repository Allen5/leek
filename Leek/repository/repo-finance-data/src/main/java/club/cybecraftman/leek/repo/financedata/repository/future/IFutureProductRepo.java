package club.cybecraftman.leek.repo.financedata.repository.future;

import club.cybecraftman.leek.infrastructure.database.datasource.FinanceDataDataSourceConfig;
import club.cybecraftman.leek.repo.financedata.model.future.FutureProduct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(FinanceDataDataSourceConfig.class)
public interface IFutureProductRepo extends JpaRepository<FutureProduct, Long> {
}
