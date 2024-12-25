package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestCapitalCurrentRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestDailyStatRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestRecordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Optional;

/**
 * 回测日统计业务服务
 */
@Service
@Slf4j
public class BackTestDailyStatService {

    // 基准为 2% 年化利率的储蓄
    private static final BigDecimal BENCHMARK_RATE = new BigDecimal("0.02");

    @Autowired
    private IBackTestDailyStatRepo dailyStatRepo;

    @Autowired
    private IBackTestRecordRepo recordRepo;

    @Autowired
    private IBackTestCapitalCurrentRepo capitalCurrentRepo;

    /**
     * 统计当日的回测数据
     * @param market
     * @param financeType
     * @param recordId
     * @param currentDate
     */
    @Transactional
    public void statDaily(final Market market, final FinanceType financeType, final Long recordId, final Date currentDate) {
        // step1: 获取回测记录
        Optional<BackTestRecord> op = recordRepo.findById(recordId);
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不存在对应的回测记录: " + recordId);
        }
        // 获取当前持仓单
        BackTestRecord record = op.get();
        BackTestDailyStat stat = new BackTestDailyStat();
        stat.setRecordId(recordId);
        stat.setDate(currentDate);
        stat.setBenchmark(calcBenchmark(record.getInitCapital()));
        // 计算当日净收益
        stat.setNet(capitalCurrentRepo.sumDailyNet(recordId, currentDate));
        // 计算当日手续费服务费
        stat.setCommission(capitalCurrentRepo.sumDailyCommission(recordId, currentDate));
        // 计算当日收益: 净收益 + 手续费
        stat.setProfit(stat.getNet().add(stat.getCommission()));
        // 计算当日资产总值: (收入 - 支出) + 初始资金
        BigDecimal totalIncome = capitalCurrentRepo.sumTotalIncome(recordId);
        BigDecimal totalOutcome = capitalCurrentRepo.sumTotalOutcome(recordId);
        BigDecimal capital = totalIncome.subtract(totalOutcome).add(record.getInitCapital());
        stat.setCapital(capital);
        dailyStatRepo.save(stat);
    }

    /**
     * Tips: 此处忽略复利计算，仅考虑基准收益
     * @param initCapital
     * @return
     */
    private BigDecimal calcBenchmark(final BigDecimal initCapital) {
        // 折算为日收益
        return initCapital.multiply(BENCHMARK_RATE.divide(BigDecimal.valueOf(360), 6, RoundingMode.HALF_UP));
    }


}
