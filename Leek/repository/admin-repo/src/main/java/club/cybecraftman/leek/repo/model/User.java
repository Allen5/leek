package club.cybecraftman.leek.repo.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "admin_user")
@Data
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色Id
     */
    @Column(nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 32)
    private String password;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date updatedAt;

}
