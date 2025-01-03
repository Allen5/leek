package club.cybercraftman.leek.common.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * 通用线程池
 */
@Component
@Slf4j
public class CommonThreadPool {

    private ExecutorService executor;

    /**
     * 使用线程池执行任务
     * TODO: 将线程池的size设置配置化
     * @param abstractTasks 执行任务
     */
    public <T extends AbstractTask> void execute(List<T> abstractTasks) {
        CountDownLatch latch = new CountDownLatch(abstractTasks.size());
        for (AbstractTask task: abstractTasks) {
            task.setLatch(latch);
            getExecutor().submit(task);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private ExecutorService getExecutor() {
        if ( null != this.executor ) {
            return this.executor;
        }
        this.executor = new ThreadPoolExecutor(32,
                32,
                100L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>());
        return this.executor;
    }


}
