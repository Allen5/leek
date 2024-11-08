package club.cybecraftman.leek.repo.future.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "future_exchange")
@Data
@ToString
public class FutureExchange {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    /**
     * 金融市场
     * @see club.cybecraftman.leek.common.constant.finance.Market
     */
    @Column(name = "market_code", nullable = false, length = 8)
    private String marketCode;

    /**
     * 交易所代码
     */
    @Column(name = "code", nullable = false, unique = true, length = 8)
    private String code;

    /**
     * 品种名称
     */
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    /**
     * 官网地址
     */
    @Column(name = "website")
    private String website;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private Date updatedAt;


}
