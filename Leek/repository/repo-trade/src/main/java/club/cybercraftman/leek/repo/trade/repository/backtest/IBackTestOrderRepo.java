package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestOrderRepo extends JpaRepository<BackTestOrder, Long> {

    @Query("select a from BackTestOrder a where a.recordId = :recordId and a.status = :status")
    List<BackTestOrder> findAllByRecordIdAndStatus(final @Param("recordId") Long recordId,
                                                   final @Param("status") Integer status);
}
