package club.cybercraftman.leek.job;

import lombok.Getter;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class BaseJob {

    @Getter
    @Autowired
    private Environment environment;

    @Getter
    private StreamExecutionEnvironment flink;

    public String getId() {
        return this.getClass().getName();
    }

    public void etl(StreamExecutionEnvironment flink) {
        this.flink = flink;
        this.checkParams();
        this.handle();
    }

    /**
     * 设置Flink环境运行参数
     */
    public void setFlinkEnvironments() {
        // 批处理模式
        this.flink.setRuntimeMode(RuntimeExecutionMode.BATCH);
    }

    /**
     * 校验自有参数
     */
    public abstract void checkParams();

    /**
     * 执行ETL
     */
    public abstract void handle();

}
