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
    private String creeper;

    private String marketCode;

    private String financeType;

    private String dataType;

    private String sourceName;

    private String source;

    private Integer status;

    private String errCause;

    private Date createdAt;

    private Date updatedAt;

}
