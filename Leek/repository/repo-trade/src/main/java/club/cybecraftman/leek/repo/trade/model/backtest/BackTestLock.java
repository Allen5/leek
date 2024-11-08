package club.cybecraftman.leek.repo.trade.model.backtest;

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
    @Column(name = "lock_id", nullable = false, length = 32)
    private String lockId;

    /**
     * 加锁的时间
     */
    @Column(name = "lock_time", nullable = false)
    private Date lockTime;

}
