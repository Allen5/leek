package club.cybecraftman.leek.domain.backtest.order;

import club.cybecraftman.leek.common.constant.Direction;
import club.cybecraftman.leek.core.order.IOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BackTestOrder implements IOrder {

    @Override
    public String order(Direction direction, String symbol, Integer count) {
        return "";
    }

    @Override
    public void cancel(String orderId) {

    }

}
