package club.cybercraftman.leek.job;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.infrastructure.compute.constant.FileSystemSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class FutureContractEtl extends BaseJob {

    private FileSystemSupport fileSystem;

    private String filepath;


    @Override
    public void checkParams() {
        // 合约文件名称
        this.filepath = getEnvironment().getRequiredProperty("filepath");
        if ( !StringUtils.hasText(this.filepath) ) {
            throw new LeekRuntimeException("请输入待清洗的合约文件信息.当前为空");
        }
        this.fileSystem = FileSystemSupport.parse(this.filepath);
    }

    @Override
    public void handle() {
        log.info("====> 开始执行期货合约ETL");
        // step1: 从minio中读取数据
        loadContract();
        // step2: 创建临时的合约表
        // step3: 读取calendar。 market_code, future_type
        // step4: 执行清洗SQL
        rebuild();
        // step5: sink到 leek_finance_data的future_contract表
        log.info("====> 期货合约ETL执行完成");
    }

    /**
     * 加载合约信息
     */
    private void loadContract() {
        log.info("====> 从minio中读取合约信息文件: {}。 文件系统: {}", filepath, this.fileSystem);
        String tableSQL = "CREATE TABLE ods_future_contract (" +
                "instrument STRING," +
                "delist_date DATE," +
                "exchange STRING," +
                "last_deliver_date DATE," +
                "list_date DATE," +
                "multiplier DECIMAL," +
                "name STRING," +
                "price_tick DECIMAL," +
                "product_code STRING," +
                "trading_code STRING," +
                "uinstrument STRING) " +
                " WITH (" +
                "'connector'='filesystem'," +
                "'path'='#FILEPATH#'," +
                "'format'='csv'" +
                ")";
        tableSQL = tableSQL.replaceAll("#FILEPATH#", this.filepath);
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(this.getFlink());
        tableEnvironment.executeSql(tableSQL);
        Table table = tableEnvironment.sqlQuery("select instrument, name from ods_future_contract");
        tableEnvironment.toDataStream(table).print();
    }


    private void rebuild() {
        String sql = "SELECT " +
                "(select date from calendar where market_code = 'CN' and date <= t.last_nature_date order by date desc limit 1 ) as last_trade_date," +
                "t.last_nature_date," +
                "t.product_code," +
                "t.name," +
                "t.list_date," +
                "t.delist_date," +
                "t.last_deliver_date," +
                "t.multiplier," +
                "t.price_tick," +
                "t.exchange_code" +
                "from" +
                "(" +
                "SELECT" +
                "product_code," +
                "name," +
                "list_date," +
                "delist_date," +
                "last_deliver_date," +
                "last_day(last_deliver_date - interval 1 month) as last_nature_date," +
                "multiplier," +
                "price_tick," +
                "exchange_code" +
                "from" +
                "future_contract) t";
    }

}
