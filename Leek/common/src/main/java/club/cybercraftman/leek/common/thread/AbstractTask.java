package club.cybercraftman.leek.common.thread;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public abstract class AbstractTask implements Runnable {

    @Setter
    private CountDownLatch latch;

    @Getter
    private ExecuteResult result;

    @Override
    public void run() {
        try {
            this.execute();
        } catch (Exception e) {
            log.error("[Thread: {}-{}]任务执行失败",
                    Thread.currentThread().getId(),
                    Thread.currentThread().getName(), e);
            this.result = ExecuteResult.builder()
                    .threadId(Thread.currentThread().getId())
                    .threadName(Thread.currentThread().getName())
                    .errCause(e.getLocalizedMessage())
                    .build();
        } finally {
            if ( latch != null ) {
                log.debug("[Thread: {}-{}]任务执行完成",
                        Thread.currentThread().getId(),
                        Thread.currentThread().getName());
                latch.countDown();
            }
        }
    }

    /**
     * 子类实现execute完成业务逻辑
     */
    protected abstract void execute();
}
