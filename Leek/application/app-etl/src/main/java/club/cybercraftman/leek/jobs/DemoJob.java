package club.cybercraftman.leek.jobs;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DemoJob {

    public static void main(String[] args) throws AnalysisException {
        SparkSession session = SparkSession.builder()
                .appName("Demo Job").master("spark://0.0.0.0:7070").getOrCreate();
        Dataset<Row> contract = session.read().format("jdbc")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("url", "jdbc:mysql://localhost:3306/leek_finance_data?useCursorFetch=true")
                .option("user", "leek_dev")
                .option("password", "leek_dev")
                .option("dbtable", "future_contract")
                .option("fetchsize", 10000)
                .load();
        Dataset<Row> calendar = session.read().format("jdbc")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("url", "jdbc:mysql://localhost:3306/leek_finance_data?useCursorFetch=true")
                .option("user", "leek_dev")
                .option("password", "leek_dev")
                .option("dbtable", "calendar")
                .option("fetchsize", 10000)
                .load();
        calendar.createTempView("temp_calendar");
        contract.createTempView("temp_future_contract");
        // 创建临时视图
        session.sql("SELECT   " +
                        " (select max(date) from temp_calendar where market_code = 'CN' and date <= t.last_nature_date) as last_trade_date, " +
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
                        "  temp_future_contract) t;")
                .write()
                .format("jdbc")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("url", "jdbc:mysql://localhost:3306/leek_finance_data?useCursorFetch=true")
                .option("user", "leek_dev")
                .option("password", "leek_dev")
                .option("dbtable", "ods_future_contract")
                .save();
    }

}
