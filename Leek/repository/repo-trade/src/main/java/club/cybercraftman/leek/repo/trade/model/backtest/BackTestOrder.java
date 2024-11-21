package club.cybercraftman.leek.repo.trade.model.backtest;

import club.cybercraftman.leek.common.constant.finance.OrderStatus;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "backtest_order")
@Data
@ToString
public class BackTestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回测记录Id
     */
    @Column(name = "record_id", nullable = false, length = 32)
    private Long recordId;

    /**
     * 交易代码
     */
    @Column(name = "symbol", nullable = false, length = 8)
    private String symbol;

    /**
     * 订单状态
     * @see OrderStatus
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
