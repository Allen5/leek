package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.core.order.IOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BackTestOrderService implements IOrder {

    @Override
    public String order(Direction direction, String symbol, Integer count) {
        return "";
    }

    @Override
    public void cancel(String orderId) {

    }

}
