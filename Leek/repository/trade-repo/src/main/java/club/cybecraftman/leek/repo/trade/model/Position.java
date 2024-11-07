package club.cybecraftman.leek.repo.trade.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "trade_position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    /**
     * @see club.cybecraftman.leek.common.constant.Direction
     */
    private Integer direction;

    /**
     * 市场代码
     * @see club.cybecraftman.leek.common.constant.Market
     */
    private String marketCode;

    /**
     * 金融产品类型
     * @see club.cybecraftman.leek.common.constant.FinanceType
     */
    private String financeType;

    /**
     * 交易代码
     */
    private String symbol;

    /**
     * 持仓数量
     */
    private Integer count;

    /**
     * 开仓价格
     */
    private BigDecimal openPrice;

    /**
     * 平仓价格
     */
    private BigDecimal closePrice;

    /**
     * 开仓时间
     */
    private Date openedAt;

    /**
     * 平仓时间
     */
    private Date closedAt;

}
