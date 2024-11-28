package club.cybercraftman.leek.repo.monitor.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.MonitorDataSourceConfig;
import club.cybercraftman.leek.repo.monitor.model.CreepLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
@ConditionalOnBean(MonitorDataSourceConfig.class)
public interface ICreeperLog extends JpaRepository<CreepLog, Long> {

    @Query("select t from CreepLog t where t.marketCode = :market" +
            " and t.financeType = :financeType" +
            " and t.status in (:status)" +
            " and t.dataType = :dataType" +
            " and t.updatedAt >= :start and t.updatedAt <= :end")
    List<CreepLog> findAllByMarketCodeAndFinanceTypeAndDataTypeAndStatusAndUpdatedAt(final @Param("market") String marketCode,
                                                                                     final @Param("financeType") String financeType,
                                                                                     final @Param("dataType") String dataType,
                                                                                     final @Param("status") List<Integer> status,
                                                                                     final @Param("start") Date start,
                                                                                     final @Param("end") Date end);

}
