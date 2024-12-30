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
public class FutureBackTestEtlJob extends BaseSparkJob {

    private static final String FUTURE_BAR_1D_VIEW = "tmp_future_bar_1d";
    private static final String FUTURE_CONTRACT_VIEW = "tmp_future_contract";

    @Override
    protected void execute(SparkSession session) throws LeekException {
        DataSourceProperties properties = SpringContextUtil.getBean("financeDataDataSourceProperties");
        try {
            // step1: load future_bar_1d, ods_future_contract
            this.loadWithJdbcToView(session, "future_bar_1day", FUTURE_BAR_1D_VIEW, properties);
            this.loadWithJdbcToView(session, "ods_future_contract", FUTURE_CONTRACT_VIEW, properties);
            // step2: transform
            Dataset<Row> dataset = this.transform(session);
            // step3: sink
            this.sinkWithJdbc(dataset, "ods_future_backtest", SaveMode.Overwrite, properties);
        } catch (AnalysisException e) {
            log.error("期货回测数据处理失败. ", e);
            throw new LeekException(e.getMessage());
        }
    }

    @Override
    protected String getId() {
        return "F10001";
    }

    @Override
    protected String getName() {
        return "期货回测数据生成";
    }
    
    private Dataset<Row> transform(SparkSession session) {
        String sql = "SELECT " +
                "  symbol,  " +
                "  product_code,  " +
                "  name,  " +
                "  datetime,  " +
                "  case rn when 1 then 1 else 0 end as main_contract, " +
                "  last_deliver_date,  " +
                "  last_trade_date,  " +
                "  volume,  " +
                "  open_interest,  " +
                "  open,  " +
                "  high,  " +
                "  low,  " +
                "  close,  " +
                "  settle,  " +
                "  amount,  " +
                "  exchange_code,  " +
                "  multiplier,  " +
                "  price_tick, " +
                "  delist_date " +
                "FROM (  " +
                "  " +
                " SELECT  " +
                " ROW_NUMBER() over (partition by t2.product_code, t2.datetime order by total_rank) as rn,  " +
                " t2.* from (  " +
                "  SELECT   " +
                "  t.datetime,  " +
                "  t.symbol,  " +
                "  (t.volume_rank + t.open_interest_rank) as total_rank,  " +
                "  t.volume,  " +
                "  t.last_deliver_date,  " +
                "  t.last_trade_date,  " +
                "  t.name,  " +
                "  t.open,  " +
                "  t.high,  " +
                "  t.low,  " +
                "  t.close,  " +
                "  t.settle,  " +
                "  t.amount,  " +
                "  t.product_code,  " +
                "  t.exchange_code,  " +
                "  t.multiplier,  " +
                "  t.price_tick,  " +
                "  t.open_interest, " +
                "  t.delist_date    " +
                "  from (  " +
                "  SELECT  " +
                "    a.volume as volume ,  " +
                "    rank() over ( partition by a.product_code, a.`datetime` order by volume desc) as volume_rank,  " +
                "    a.open_interest as open_interest ,  " +
                "    rank() over ( partition by a.product_code, a.`datetime` order by open_interest desc) as open_interest_rank,  " +
                "    a.datetime as `datetime` ,  " +
                "    a.symbol as symbol ,  " +
                "    b.last_deliver_date as last_deliver_date,  " +
                "    b.last_trade_date as last_trade_date,  " +
                "    b.name as name,  " +
                "    a.open as open,  " +
                "    a.high as high,  " +
                "    a.low as low,  " +
                "    a.`close` as `close` ,  " +
                "    a.settle as settle ,  " +
                "    a.amount as amount ,  " +
                "    a.product_code as product_code ,  " +
                "    b.exchange_code as exchange_code,  " +
                "    b.multiplier as multiplier,  " +
                "    b.price_tick as price_tick,  " +
                "    b.delist_date as delist_date " +
                "  from #futureBar1d# a left join #futureContract# b on a.symbol = b.code  " +
                "  where last_deliver_date is not null and a.`datetime` <= last_trade_date  " +
                "  order by datetime) t  " +
                ") t2  " +
                ") t3";
        sql = sql.replaceAll("#futureBar1d#", FUTURE_BAR_1D_VIEW)
                .replaceAll("#futureContract#", FUTURE_CONTRACT_VIEW);
        return session.sql(sql);
    }

}
