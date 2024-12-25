package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
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

    /**
     * 计算总收益
     * @param recordId
     * @return
     */
    @Query("select COALESCE(sum(a.profit), 0) from BackTestDailyStat a where a.recordId = :recordId")
    BigDecimal sumProfit(final @Param("recordId") Long recordId);

    /**
     * 计算净收益
     * @param recordId
     * @return
     */
    @Query("select COALESCE(sum(a.net), 0) from BackTestDailyStat a where a.recordId = :recordId")
    BigDecimal sumNet(final @Param("recordId") Long recordId);

    /**
     * 计算总手续费
     * @param recordId
     * @return
     */
    @Query("select COALESCE(sum(a.commission), 0) from BackTestDailyStat a where a.recordId = :recordId")
    BigDecimal sumCommission(final @Param("recordId") Long recordId);

    /**
     * 获取某一时点的资产总值
     */
    @Query("select COALESCE(a.capital, 0) from BackTestDailyStat a where a.recordId = :recordId and a.date = :date")
    BigDecimal getCapital(final @Param("recordId") Long recordId, final @Param("date") Date date);

}
