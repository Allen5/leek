package club.cybecraftman.leek.repo.monitor.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "monitor_creep_log")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class CreepLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 爬虫类名
     */
    @Column(name = "creeper", nullable = false)
    private String creeper;

    @Column(name = "market_code", nullable = false, length = 8)
    private String marketCode;

    @Column(name = "finance_type", nullable = false, length = 8)
    private String financeType;

    @Column(name = "data_type", nullable = false, length = 8)
    private String dataType;

    @Column(name = "source_name", nullable = false, length = 20)
    private String sourceName;

    @Column(name = "source", length = 1024)
    private String source;

    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    @Column(name = "err_cause", length = 4096)
    private String errCause;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
