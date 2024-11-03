package club.cybecraftman.leek.repo.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "future_user_trade_tax")
@Data
@ToString
public class FutureUserTradeTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户Id
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 合约代码
     */
    @Column(nullable = false)
    private String contractCode;

    /**
     * 手续费比例
     */
    private BigDecimal rate;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
