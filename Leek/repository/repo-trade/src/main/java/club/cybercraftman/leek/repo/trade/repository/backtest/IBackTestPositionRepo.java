package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ConditionalOnBean(TradeDataSourceConfig.class)
public interface IBackTestPositionRepo extends JpaRepository<BackTestPosition, Long> {

    /**
     * 查询对应状态的持仓记录数
     * @param recordId
     * @param symbol
     * @param status
     * @return
     */
    @Query("select count(1) from BackTestPosition a where a.recordId = :recordId and a.symbol = :symbol and a.status = :status")
    Integer countByRecordIdAndSymbolAndStatus(final @Param("recordId") Long recordId,
                                              final @Param("symbol") String symbol,
                                              final @Param("status") Integer status);


    @Query("select a from BackTestPosition a where a.recordId = :recordId and symbol = :symbol ")
    Optional<BackTestPosition> findOneByRecordIdAndSymbol(final @Param("recordId") Long recordId,
                                                          final @Param("symbol") String symbol);

}
