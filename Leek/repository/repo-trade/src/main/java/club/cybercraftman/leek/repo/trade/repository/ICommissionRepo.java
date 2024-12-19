package club.cybercraftman.leek.repo.trade.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.Commission;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface ICommissionRepo extends JpaRepository<Commission, Long> {

    @Query("select a from Commission a where a.marketCode = :market and a.financeType = :financeType and a.status = :status")
    List<Commission> findAllByStatus(final @Param("market") String market,
                                     final @Param("financeType") String financeType,
                                     final @Param("status") Integer status);

}
