package club.cybercraftman.leek.repo.financedata.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.FinanceDataDataSourceConfig;
import club.cybercraftman.leek.repo.financedata.model.future.FutureBackTest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.Date;
import java.util.List;

@Repository
@ConditionalOnBean(FinanceDataDataSourceConfig.class)
public interface IFutureBackTestRepo extends JpaRepository<FutureBackTest, Long> {

    @Query(value = "select count(1) as bars, product_code as productCode from ods_future_backtest where datetime >= :start and datetime <= :end group by product_code having bars >= :minBars",
            nativeQuery = true)
    List<Tuple> findProductCodesLargeThan(final @Param("minBars") int minBars,
                                          final @Param("start") Date start,
                                          final @Param("end") Date end);

    /**
     * 计算数量
     * @param code
     * @return
     */
    @Query("select count(a) from FutureBackTest a where a.productCode = :productCode")
    Long countByCode(final @Param("productCode") String code);

    /**
     * 计算数量
     * @param code
     * @return
     */
    @Query("select count(a) from FutureBackTest a where a.productCode = :productCode and datetime >= :start and datetime <= :end")
    Integer countByCodeAndDateTimeRange(final @Param("productCode") String code,
                                     final @Param("start") Date start,
                                     final @Param("end") Date end);

    @Query(value = "select min(datetime) as start, max(datetime) as end from " +
            " (select * from ods_future_backtest " +
            "   where product_code = :productCode order by datetime limit :offset, :size) t", nativeQuery = true)
    Tuple findDateRangeByCode(final @Param("productCode") String code,
                              final @Param("offset") Integer offset,
                              final @Param("size") Integer size);

    @Query("select a from FutureBackTest a where a.symbol = :symbol and a.datetime = :date")
    FutureBackTest findOneByDateAndSymbol(final @Param("date") Date date,
                                          final @Param("symbol") String symbol);

    /**
     * 获取主力合约
     * @param productCode
     * @param datetime
     * @return
     */
    @Query("select a from FutureBackTest a " +
            " where a.productCode = :productCode " +
            " and a.datetime = :datetime " +
            " and a.isMainContract = 1")
    FutureBackTest findMainByDateAndCode(final @Param("productCode") String productCode,
                                         final @Param("datetime") Date datetime);

    /**
     * 获取周期行情数据
     * @param symbol
     * @param minDate
     * @param maxDate
     * @return
     */
    @Query("select a from FutureBackTest a where a.symbol = :symbol and a.datetime >= :minDate and a.datetime <= :maxDate ")
    List<FutureBackTest> findAllBySymbolAndDateRange(final @Param("symbol") String symbol,
                                                     final @Param("minDate") Date minDate,
                                                     final @Param("maxDate") Date maxDate);

}