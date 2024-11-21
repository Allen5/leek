package club.cybercraftman.leek.common.thread;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class ExecuteResult {

    private Long threadId;

    private String threadName;

    /**
     * 错误信息
     */
    private String errCause;

}
