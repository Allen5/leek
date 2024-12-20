package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.core.position.IPosition;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class BackTestPositionService implements IPosition {

    @Autowired
    private IBackTestPositionRepo backTestPositionRepo;

    /**
     * 检查是否有持仓
     * @param recordId
     * @param symbol
     * @return
     */
    public boolean hasPosition(Long recordId, String symbol) {
        Integer count = backTestPositionRepo.countByRecordIdAndSymbolAndStatus(recordId, symbol, PositionStatus.OPEN.getStatus());
        return count != null && count > 0;
    }

    @Override
    public void open(String symbol, Direction direction, Integer count, BigDecimal price) {

    }

    @Override
    public void close(String symbol, BigDecimal price) {

    }

}
