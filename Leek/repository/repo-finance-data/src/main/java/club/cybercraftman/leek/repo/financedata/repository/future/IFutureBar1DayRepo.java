package club.cybercraftman.leek.repo.financedata.repository.future;

import club.cybercraftman.leek.infrastructure.database.datasource.FinanceDataDataSourceConfig;
import club.cybercraftman.leek.repo.financedata.model.future.FutureBar1Day;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
@ConditionalOnBean(FinanceDataDataSourceConfig.class)
public interface IFutureBar1DayRepo extends JpaRepository<FutureBar1Day, Long> {

    @Transactional
    @Modifying
    @Query("delete from FutureBar1Day t where t.datetime = :datetime and t.symbol = :symbol")
    void deleteByDateTimeAndSymbol(final @Param("datetime") Date datetime,
                                   final @Param("symbol") String symbol);

    @Transactional
    @Modifying
    @Query("delete from FutureBar1Day t where t.datetime >= :start and t.datetime <= :end")
    void deleteAllByDateTime(final @Param("start") Date start, final @Param("end") Date end);

}
