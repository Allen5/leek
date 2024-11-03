package club.cybecraftman.leek.repo.model;

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
    @Column(nullable = false)
    private Long recordId;

    /**
     * 交易代码
     */
    private String symbol;

    /**
     * 订单状态
     * @see club.cybecraftman.leek.common.constant.OrderStatus
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Date createdAt;

    private Date updatedAt;

}
