package club.cybecraftman.leek.repo.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "future_exchange")
@Data
@ToString
public class FutureExchange {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    /**
     * 品种代码
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * 品种名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 官网地址
     */
    private String website;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
