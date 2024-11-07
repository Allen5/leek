package club.cybecraftman.leek.core.order;

import club.cybecraftman.leek.common.constant.finance.Direction;

/**
 * 订单接口
 * 由具体服务实现，回测服务实现回测的下单逻辑
 */
public interface IOrder {

    /**
     * 下单接口
     * @param direction 交易方向
     * @param symbol 交易代码
     * @param count 交易数量
     * @return 订单ID
     */
    String order(final Direction direction, final String symbol, final Integer count);

    /**
     * 撤单接口
     * @param orderId 订单ID
     */
    void cancel(final String orderId);

}
