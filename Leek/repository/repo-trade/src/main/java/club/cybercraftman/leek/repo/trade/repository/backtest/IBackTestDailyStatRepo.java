package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestDailyStatRepo extends JpaRepository<BackTestDailyStat, Long> {

    /**
     * 获取回测日统计
     * @param recordId
     * @return
     */
    @Query("select a from BackTestDailyStat a where a.recordId = :recordId")
    List<BackTestDailyStat> findAllByRecordId(final @Param("recordId") Long recordId);

}
