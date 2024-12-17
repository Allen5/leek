package club.cybercraftman.leek.domain.financedata.jobs;

import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.infrastructure.compute.job.BaseSparkJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FutureContractEtlJob extends BaseSparkJob {

    private static final String FUTURE_CONTRACT_VIEW = "tmp_future_contract";
    private static final String FUTURE_CALENDAR_VIEW = "tmp_calendar";

    @Override
    protected void execute(SparkSession session) throws LeekException {
        // step1: 读取future_contract和calendar数据，生成temp表
        try {
            this.load(session, "future_contract", FUTURE_CONTRACT_VIEW);
            this.load(session, "calendar", FUTURE_CALENDAR_VIEW);
            // step2: 执行清洗逻辑
            Dataset<Row> data = this.transform(session);
            // step3: 写入目标
            this.sink(data);
        } catch (Exception e) {
            log.error("期货合约清洗失败. ", e);
            throw new LeekException(e.getMessage());
        }
    }

    @Override
    protected String getId() {
        return "F10000";
    }

    @Override
    protected String getName() {
        return "期货合约清洗任务";
    }

    private void load(SparkSession session, String oriTableName, String tmpTableName) throws AnalysisException {
        DataSourceProperties props = SpringContextUtil.getBean("financeDataDataSourceProperties");
        Dataset<Row> data = session.read().format("jdbc")
                .option("driver", props.getDriverClassName())
                .option("url", decorateJdbcUrl(props.getUrl()))
                .option("user", props.getUsername())
                .option("password", props.getPassword())
                .option("dbtable", oriTableName)
                .option("fetchsize", FETCH_SIZE)
                .load();
        data.createTempView(tmpTableName);
    }

    private Dataset<Row> transform(SparkSession session) {
        String sql = "SELECT   " +
                " (select max(date) from #calendarView# where market_code = 'CN' and date <= t.last_nature_date) as last_trade_date, " +
                " t.last_nature_date, " +
                " t.product_code, " +
                " t.name, " +
                " t.list_date, " +
                " t.delist_date, " +
                " t.last_deliver_date, " +
                " t.multiplier, " +
                " t.price_tick, " +
                " t.exchange_code " +
                " FROM " +
                " (SELECT " +
                "  product_code, " +
                "  name, " +
                "  list_date, " +
                "  delist_date, " +
                "  last_deliver_date, " +
                "  last_day(last_deliver_date - interval 1 month) as last_nature_date, " +
                "  multiplier, " +
                "  price_tick, " +
                "  exchange_code " +
                " FROM " +
                "  #futureContractView#) t";
        sql = sql.replaceAll("#calendarView#", FUTURE_CALENDAR_VIEW)
                .replaceAll("#futureContractView#", FUTURE_CONTRACT_VIEW);
        return session.sql(sql);
    }

    private void sink(Dataset<Row> dataset) {
        DataSourceProperties props = SpringContextUtil.getBean("financeDataDataSourceProperties");
        dataset.write()
                .mode(SaveMode.Overwrite)
                .format("jdbc")
                .option("driver", props.getDriverClassName())
                .option("url", decorateJdbcUrl(props.getUrl()))
                .option("user", props.getUsername())
                .option("password", props.getPassword())
                .option("dbtable", "ods_future_contract")
                .save();
    }

}
