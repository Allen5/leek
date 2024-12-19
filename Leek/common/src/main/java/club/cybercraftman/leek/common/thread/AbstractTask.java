package club.cybercraftman.leek.common.thread;

import club.cybercraftman.leek.common.exception.LeekException;
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
            this.onSuccess();
        } catch (Exception e) {
            log.error("[Thread: {}-{}]任务执行失败",
                    Thread.currentThread().getId(),
                    Thread.currentThread().getName(), e);
            this.result = ExecuteResult.builder()
                    .threadId(Thread.currentThread().getId())
                    .threadName(Thread.currentThread().getName())
                    .errCause(e.getLocalizedMessage())
                    .build();
            this.onFail(e.getMessage());
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
    protected abstract void execute() throws LeekException;

    /**
     * 执行失败处理
     */
    protected abstract void onFail(String message);

    /**
     * 执行成功处理
     */
    protected abstract void onSuccess();
}
