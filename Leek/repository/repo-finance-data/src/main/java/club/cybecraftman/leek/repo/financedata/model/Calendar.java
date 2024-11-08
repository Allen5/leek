package club.cybecraftman.leek.repo.financedata.model;

import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * 交易日期
 */
@Entity
@Table(name = "calendar", uniqueConstraints = {
        @UniqueConstraint(name = "uc_calendar_date_marketCode", columnNames = {"date", "market_code", "finance_type"})
})
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    /**
     * 市场代码
     * @see Market
     */
    @Column(name = "market_code", nullable = false, length = 4)
    private String marketCode;

    /**
     * 金融产品类型
     * @see FinanceType
     */
    @Column(name = "finance_type", nullable = false, length = 20)
    private String financeType;

}
