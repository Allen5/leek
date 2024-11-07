package club.cybecraftman.leek.repo.meta.model;

import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import lombok.*;

import javax.persistence.*;

/**
 * 交易日期
 */
@Entity
@Table(name = "meta_calendar")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Calendar {

    @Id
    private String date;

    /**
     * 市场代码
     * @see Market
     */
    private String marketCode;

    /**
     * 金融产品类型
     * @see FinanceType
     */
    private String financeType;

}
