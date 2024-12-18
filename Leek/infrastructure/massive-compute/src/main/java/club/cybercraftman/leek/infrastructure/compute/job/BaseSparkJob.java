package club.cybercraftman.leek.infrastructure.compute.job;

import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.infrastructure.compute.spark.SessionParam;
import club.cybercraftman.leek.infrastructure.compute.spark.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

@Slf4j
public abstract class BaseSparkJob extends AbstractEtlJob {

    protected static final Integer FETCH_SIZE = 10000;

    @Override
    public void action() {
        SessionParam params = this.buildParams();
        SparkSession session = SessionUtil.getInstance().init(getId(), getMasterUrl(), params);
        try {
            log.info("开始执行任务[id: {}, name: {}. master: {}, params: {}]", getId(), getName(), getMasterUrl(), params);
            this.execute(session);
            log.info("任务执行结束[id: {}, name: {}. master: {}, params: {}]", getId(), getName(), getMasterUrl(), params);
        } catch (LeekException | LeekRuntimeException e) {
            log.error("执行Spark任务失败. 任务ID: {}, 任务名称: {} 异常信息: {}", getId(), getName(), e.getMessage(), e);
        } finally {
            SessionUtil.getInstance().release();
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

    /**
     * 对原始的JDBC url进行一些修正。对于mysql，增加useFetchCursor=true
     * @param url
     * @return
     */
    protected String decorateJdbcUrl(String url) {
        String splitter = url.contains("?") ? "&" : "?";
        if ( url.startsWith("jdbc:mysql") && !url.contains("useCursorFetch") ) {
            url = url + splitter + "useCursorFetch=true";
        } else if ( url.contains("useCursorFetch=false") ) {
            url = url.replace("useCursorFetch=false", "useCursorFetch=true");
        }
        return url;
    }

    /**
     * 从JDBC中加载数据，并创建tempView
     * @param session
     * @param oriTableName
     * @param tmpTableName
     * @param properties
     */
    protected void loadWithJdbcToView(final SparkSession session, String oriTableName, String tmpTableName, DataSourceProperties properties) throws AnalysisException {
        Dataset<Row> data = session.read().format("jdbc")
                .option("driver", properties.getDriverClassName())
                .option("url", decorateJdbcUrl(properties.getUrl()))
                .option("user", properties.getUsername())
                .option("password", properties.getPassword())
                .option("dbtable", oriTableName)
                .option("fetchsize", FETCH_SIZE)
                .load();
        data.createTempView(tmpTableName);
    }

    /**
     * 写入目标数据源
     * @param dataset
     * @param tableName
     * @param saveMode
     * @param properties
     */
    protected void sinkWithJdbc(final Dataset<Row> dataset, final String tableName, final SaveMode saveMode, final DataSourceProperties properties) {
        dataset.write()
                .mode(saveMode)
                .format("jdbc")
                .option("driver", properties.getDriverClassName())
                .option("url", decorateJdbcUrl(properties.getUrl()))
                .option("user", properties.getUsername())
                .option("password", properties.getPassword())
                .option("dbtable", tableName)
                .save();
    }

}
