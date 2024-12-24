package club.cybercraftman.leek.repo.trade.repository.backtest;

import club.cybercraftman.leek.infrastructure.database.datasource.TradeDataSourceConfig;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    @Query("select sum(availableVolume) from BackTestPosition a where a.recordId = :recordId and a.symbol = :symbol and a.status = :status")
    Long sumVolumeByRecordIdAndSymbolAndStatus(final @Param("recordId") Long recordId,
                                                  final @Param("symbol") String symbol,
                                                  final @Param("status") Integer status);


    @Query("select a from BackTestPosition a where a.recordId = :recordId and a.symbol = :symbol ")
    Optional<BackTestPosition> findOneByRecordIdAndSymbol(final @Param("recordId") Long recordId,
                                                          final @Param("symbol") String symbol);

    /**
     * 获取对应方向的未平仓持仓列表
     * 根据更新时间倒序
     * @param recordId
     * @param symbol
     * @param direction
     * @return
     */
    @Query("select a from BackTestPosition a where a.recordId = :recordId and a.symbol = :symbol and a.direction = :direction and a.status = :status order by a.updatedAt desc")
    List<BackTestPosition> findAllByRecordIdAndSymbolAndDirectionAndStatus(final @Param("recordId") Long recordId,
                                                                           final @Param("symbol") String symbol,
                                                                           final @Param("direction") Integer direction,
                                                                           final @Param("status") Integer status);

    @Query("select a from BackTestPosition a where a.recordId = :recordId and a.status = :status")
    List<BackTestPosition> findAllByRecordIdAndStatus(final @Param("recordId") Long recordId,
                                                      final @Param("status") Integer status);

}
