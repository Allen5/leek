package club.cybercraftman.leek.domain.backtest;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.core.position.IPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BackTestPosition implements IPosition {

    @Override
    public void open(String symbol, Direction direction, Integer count, BigDecimal price) {

    }

    @Override
    public void close(String symbol, BigDecimal price) {

    }

}
