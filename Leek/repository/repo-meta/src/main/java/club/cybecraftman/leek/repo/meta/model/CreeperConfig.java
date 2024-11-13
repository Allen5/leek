package club.cybecraftman.leek.repo.meta.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "meta_creeper_config", uniqueConstraints = {
        @UniqueConstraint(name = "udx_creeper_config", columnNames = {"market_code", "finance_type", "data_type", "source_name"})
})
public class CreeperConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * @see club.cybecraftman.leek.common.constant.finance.Market
     */
    @Column(name = "market_code", nullable = false, length = 4)
    private String marketCode;

    /**
     * @see club.cybecraftman.leek.common.constant.finance.FinanceType
     */
    @Column(name = "finance_type", nullable = false, length = 20)
    private String financeType;

    /**
     * @see club.cybecraftman.leek.common.constant.creep.DataType
     */
    @Column(name = "data_type", nullable = false, length = 20)
    private String dataType;

    /**
     * 爬取的数据源名称
     */
    @Column(name = "source_name", nullable = false, length = 20)
    private String sourceName;

    /**
     * 爬取数据源地址
     */
    @Column(name = "source", nullable = false)
    private String source;

    /**
     * @see club.cybecraftman.leek.common.constant.ValidStatus
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    /**
     * 开始工作时间
     */
    @Column(name = "work_start_time")
    private Date workStartTime;

    /**
     * 结束工作时间
     */
    @Column(name = "work_end_time")
    private Date workEndTime;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalTime updatedAt;

}
