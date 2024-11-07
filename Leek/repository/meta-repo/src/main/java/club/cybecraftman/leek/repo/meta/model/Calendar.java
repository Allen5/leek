package club.cybecraftman.leek.repo.meta.model;

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
     * @see club.cybecraftman.leek.common.constant.Market
     */
    private String marketCode;

    /**
     * 金融产品类型
     * @see club.cybecraftman.leek.common.constant.FinanceType
     */
    private String financeType;

}
