package club.cybercraftman.leek.repo.admin.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity()
@Table(name = "admin_role")
@Data
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;


}
