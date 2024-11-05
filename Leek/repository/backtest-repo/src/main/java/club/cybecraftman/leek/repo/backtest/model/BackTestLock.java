package club.cybecraftman.leek.repo.backtest.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "backtest_lock")
@Data
@ToString
public class BackTestLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 锁Id
     */
    @Column(nullable = false)
    private String lockId;

    /**
     * 加锁的时间
     */
    @Column(nullable = false)
    private Date lockTime;

}
