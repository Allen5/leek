package club.cybercraftman.leek.domain.financedata.jobs;

import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.infrastructure.compute.job.BaseSparkJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FutureBackTestEtlJob extends BaseSparkJob {

    private static final String FUTURE_CONTRACT_VIEW = "tmp_future_bar_1d";
    private static final String FUTURE_CALENDAR_VIEW = "tmp_future_contract";

    @Override
    protected void execute(SparkSession session) throws LeekException {
        // step1: load future_bar_1d, ods_future_contract
        // step2: transform
        // step3: sink
    }

    @Override
    protected String getId() {
        return "F10001";
    }

    @Override
    protected String getName() {
        return "期货回测数据生成";
    }

}
