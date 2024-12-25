package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPositionClose;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestPositionCloseRepo extends JpaRepository<BackTestPositionClose, Long> {

    /**
     * 计算盈利笔数
     * @param recordId
     * @return
     */
    @Query("select count(1) from BackTestPositionClose where recordId = :recordId and net > 0")
    Integer countWinByRecordId(final @Param("recordId") Long recordId);

    /**
     * 计算总交易笔数
     * @param recordId
     * @return
     */
    @Query("select count(1) from BackTestPositionClose where recordId = :recordId")
    Integer countTotalByRecordId(final @Param("recordId") Long recordId);
}
