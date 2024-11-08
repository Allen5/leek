package club.cybecraftman.leek.repo.trade.model;

import club.cybecraftman.leek.common.constant.finance.OrderStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "trade_order")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户Id
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 订单状态
     * @see OrderStatus
     */
    private Integer status;

    @Column(nullable = false)
    private Date createdAt;

    private Date updatedAt;
}
