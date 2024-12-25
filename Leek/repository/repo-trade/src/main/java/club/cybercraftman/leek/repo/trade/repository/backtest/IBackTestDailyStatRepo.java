package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
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

    /**
     * 获取日回报率
     * @param recordId
     * @param initCapital
     * @return
     */
    @Query("select (sum(net) / :capital) as ratio, date from BackTestDailyStat where recordId = :recordId group by date order by date asc")
    List<Tuple> getReturnRateByRecordId(final @Param("recordId") Long recordId,
                                        final @Param("capital") BigDecimal initCapital);

    /**
     * 计算日期范围内的交易日数量
     * @param recordId
     * @param start
     * @param end
     * @return
     */
    @Query("select count(1) from BackTestDailyStat where recordId = :recordId and date >= :start and date <= :end")
    Integer countByRecordIdAndDateRange(final @Param("recordId") Long recordId,
                                        final @Param("start") Date start,
                                        final @Param("end") Date end);

}
