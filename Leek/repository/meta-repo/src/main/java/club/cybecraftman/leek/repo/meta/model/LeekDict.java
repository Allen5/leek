package club.cybecraftman.leek.repo.meta.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 字典表
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "meta_dict")
public class LeekDict {

    @Id
    private String code;

    private String parentCode;

    private String rootCode;

    private String value;

    private String description;

    /**
     * @see club.cybecraftman.leek.common.constant.ValidStatus
     */
    private Integer status;

    private Date createdAt;

    private Date updatedAt;

}
