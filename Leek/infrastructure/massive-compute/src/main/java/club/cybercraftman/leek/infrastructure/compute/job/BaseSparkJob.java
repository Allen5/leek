package club.cybercraftman.leek.infrastructure.compute.job;

import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.infrastructure.compute.spark.SessionParam;
import club.cybercraftman.leek.infrastructure.compute.spark.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;

@Slf4j
public abstract class BaseSparkJob extends AbstractEtlJob {

    @Override
    public void action() {
        SessionParam params = this.buildParams();
        try (SparkSession session = SessionUtil.getInstance().init(getId(), getMasterUrl(), params)) {
            log.info("开始执行任务[id: {}, name: {}. master: {}, params: {}]", getId(), getName(), getMasterUrl(), params);
            this.execute(session);
            log.info("任务执行结束[id: {}, name: {}. master: {}, params: {}]", getId(), getName(), getMasterUrl(), params);
        } catch (LeekException | LeekRuntimeException e) {
            log.error("执行Spark任务失败. 任务ID: {}, 任务名称: {} 异常信息: {}", getId(), getName(), e.getMessage(), e);
        }
    }

    protected abstract void execute(SparkSession session) throws LeekException;

    /**
     * 构建基础的Spark环境参数
     * @return
     */
    protected SessionParam buildParams() {
        return SessionParam.builder().build();
    }

}
