package club.cybercraftman.leek.repo.meta.model;

import club.cybercraftman.leek.common.constant.ValidStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

/**
 * 字典表
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "meta_dict")
public class Dict {

    @Id
    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "parent_code", nullable = false, length = 32)
    private String parentCode;

    @Column(name = "root_code", nullable = false, length = 32)
    private String rootCode;

    @Column(name = "value", nullable = false, length = 128)
    private String value;

    @Column(name = "description")
    private String description;

    /**
     * @see ValidStatus
     */
    @Column(name = "status", nullable = false, length = 4)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalTime createdAt;

    @Column(name = "updated_at")
    private LocalTime updatedAt;

}
