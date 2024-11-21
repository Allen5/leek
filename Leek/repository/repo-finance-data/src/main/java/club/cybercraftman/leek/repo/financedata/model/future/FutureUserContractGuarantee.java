package club.cybercraftman.leek.repo.financedata.model.future;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

// TODO: 转移到meta库中
@Entity(name = "future_user_contract_gurantee")
@Data
@ToString
public class FutureUserContractGuarantee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户Id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 合约代码
     */
    @Column(name = "contract_code", nullable = false, length = 8)
    private String contractCode;

    /**
     * 保证金比例
     */
    @Column(name = "rate", precision = 18, scale = 3)
    private BigDecimal rate;

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
