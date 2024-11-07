package club.cybecraftman.leek.repo.meta.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "meta_creeper", uniqueConstraints = {
        @UniqueConstraint(name = "udx_creeper", columnNames = {"market", "financeType", "dataType", "sourceName"})
})
public class Creeper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * @see club.cybecraftman.leek.common.constant.finance.Market
     */
    private String market;

    /**
     * @see club.cybecraftman.leek.common.constant.finance.FinanceType
     */
    private String financeType;

    /**
     * @see club.cybecraftman.leek.common.constant.creep.DataType
     */
    private String dataType;

    /**
     * 爬取的数据源名称
     */
    @Column(nullable = false)
    private String sourceName;

    /**
     * 爬取数据源地址
     */
    @Column(nullable = false)
    private String source;

    /**
     * @see club.cybecraftman.leek.common.constant.ValidStatus
     */
    @Column(nullable = false)
    private Integer status;

    private Date createdAt;

    private Date updatedAt;

}
