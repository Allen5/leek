package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestProfit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestProfitRepo extends JpaRepository<BackTestProfit, Long> {

    /**
     * 计算盈利次数
     * @param recordId
     * @return
     */
    @Query("select count(1) from BackTestProfit a where a.recordId = :recordId and a.profit > 0")
    Integer countWinByRecordId(final @Param("recordId") Long recordId);

    /**
     * 计算亏损次数
     * @param recordId
     * @return
     */
    @Query("select count(1) from BackTestProfit a where a.recordId = :recordId and a.profit < 0")
    Integer countLoseByRecordId(final @Param("recordId") Long recordId);

}
