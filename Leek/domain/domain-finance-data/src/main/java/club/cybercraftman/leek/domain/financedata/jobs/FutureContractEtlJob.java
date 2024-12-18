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
            DataSourceProperties properties = SpringContextUtil.getBean("financeDataDataSourceProperties");
            this.loadWithJdbcToView(session, "future_contract", FUTURE_CONTRACT_VIEW, properties);
            this.loadWithJdbcToView(session, "calendar", FUTURE_CALENDAR_VIEW, properties);
            // step2: 执行清洗逻辑
            Dataset<Row> data = this.transform(session);
            // step3: 写入目标
            this.sinkWithJdbc(data, "ods_future_contract", SaveMode.Overwrite, properties);
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

    private Dataset<Row> transform(SparkSession session) {
        String sql = "SELECT t.code," +
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
                " (SELECT code," +
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

}
