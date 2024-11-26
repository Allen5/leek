package club.cybercraftman.leek.repo.meta.repository;

import club.cybercraftman.leek.repo.meta.model.CreeperConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ICreeperConfigRepo extends JpaRepository<CreeperConfig, Long> {

    @Query("select t from CreeperConfig t " +
            " where t.marketCode = :market " +
            " and t.financeType = :financeType " +
            " and t.dataType = :dataType " +
            " and t.status = :status " +
            " and t.workStartTime <= :current and :current <= t.workEndTime ")
    List<CreeperConfig> findAllByMarketCodeAnFinanceTypeAndDataTypeAndStatusAndTime(final @Param("market") String marketCode,
                                                                                    final @Param("financeType") String financeType,
                                                                                    final @Param("dataType") String dataType,
                                                                                    final @Param("status") Integer status,
                                                                                    final @Param("current") LocalTime current);

}
