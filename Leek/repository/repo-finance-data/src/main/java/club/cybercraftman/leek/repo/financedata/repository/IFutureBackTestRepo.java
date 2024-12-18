package club.cybercraftman.leek.repo.financedata.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.FinanceDataDataSourceConfig;
import club.cybercraftman.leek.repo.financedata.model.future.FutureBackTest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnBean(FinanceDataDataSourceConfig.class)
public interface IFutureBackTestRepo extends JpaRepository<FutureBackTest, Long> {

    @Query("select count(1) as bars, a.productCode from FutureBackTest a group by productCode having bars >= :minBars")
    List<String> findProductCodesLargeThan(final @Param("minBars") int minBars);

}