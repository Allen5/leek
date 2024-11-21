package club.cybercraftman.leek.repo.financedata.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.FinanceDataDataSourceConfig;
import club.cybercraftman.leek.repo.financedata.model.Calendar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@ConditionalOnBean(FinanceDataDataSourceConfig.class)
public interface ICalendarRepo extends JpaRepository<Calendar, String> {

    /**
     * 查询指定市场和金融产品的交易日历
     * @param market
     * @param financeType
     * @return
     */
    @Query("select t from Calendar t " +
            "where t.marketCode = :market " +
            "       and t.financeType = :financeType " +
            "order by t.date")
    List<Calendar> findAllByMarketAndFinanceType(final @Param("market") String market,
                                                 final @Param("financeType") String financeType);

    /**
     * 查询指定市场和金融产品在 指定时间范围内的交易日历
     * @param marketCode
     * @param financeType
     * @param startTime
     * @param endTime
     * @return
     */
    @Query("select t from Calendar t " +
            " where t.marketCode = :market " +
            "       and t.financeType = :financeType " +
            "       and t.date >= :startTime " +
            "       and t.date <= :endTime " +
            "order by t.date")
    List<Calendar> findAllByMarketAndFinanceTypeAndDateRange(final @Param("market") String marketCode,
                                                             final @Param("financeType") String financeType,
                                                             final @Param("startTime") Date startTime,
                                                             final @Param("endTime") Date endTime);

    /**
     * 查询当前交易日
     * @param marketCode
     * @param financeType
     * @param today
     * @return
     */
    @Query("SELECT max(t.date) from Calendar t where t.date <= :today and t.marketCode = :market and t.financeType = :financeType ")
    Date findCurrentTradeDate(final @Param("market") String marketCode,
                              final @Param("financeType") String financeType,
                              final @Param("today") Date today);

}
