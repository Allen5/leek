package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.constant.finance.Direction;
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

    public boolean check(Long recordId, String symbol) {
        Optional<BackTestPosition> op = backTestPositionRepo.findOneByRecordIdAndSymbol(recordId, symbol);
        return op.isPresent();
    }


    @Override
    public void open(String symbol, Direction direction, Integer count, BigDecimal price) {

    }

    @Override
    public void close(String symbol, BigDecimal price) {

    }

}
