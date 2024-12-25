package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestCapitalCurrent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestCapitalCurrentRepo extends JpaRepository<BackTestCapitalCurrent, Long> {

    /**
     * 计算当日的净收益
     * @param recordId
     * @param datetime
     * @return
     */
    @Query("select COALESCE(sum(amount),0) from BackTestCapitalCurrent where recordId = :recordId and datetime = :datetime and type = 1 and category = 300")
    BigDecimal sumDailyNet(final @Param("recordId") Long recordId,
                           final @Param("datetime") Date datetime);

    /**
     * 计算当日支出的手续费
     * @param recordId
     * @param datetime
     * @return
     */
    @Query("select COALESCE(sum(amount),0) from BackTestCapitalCurrent where recordId = :recordId and datetime = :datetime and type = 2 and category = 100")
    BigDecimal sumDailyCommission(final @Param("recordId") Long recordId,
                                  final @Param("datetime") Date datetime);

    /**
     * 统计总收入
     * @param recordId
     * @return
     */
    @Query("select COALESCE(sum(amount), 0) from BackTestCapitalCurrent where recordId = :recordId and type = 1")
    BigDecimal sumTotalIncome(final @Param("recordId") Long recordId);

    /**
     * 统计总支出
     * @param recordId
     * @return
     */
    @Query("select COALESCE(sum(amount),0) from BackTestCapitalCurrent where recordId = :recordId and type = 2")
    BigDecimal sumTotalOutcome(final @Param("recordId") Long recordId);

}
