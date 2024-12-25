package club.cybercraftman.leek.core.eveluator;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.service.BackTestDailyStatService;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestDailyStatRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionCloseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评估工具
 */
@Component
@Slf4j
public class EvaluatorUtil {

    @Autowired
    private IBackTestDailyStatRepo dailyStatRepo;

    @Autowired
    private IBackTestPositionCloseRepo closeRepo;

    public BackTestRecord evaluate(final BackTestRecord record, final Date finishedDate) {
        // 计算策略收益
        record.setProfit(dailyStatRepo.sumProfit(record.getId()));
        // 计算策略净收益
        record.setNet(dailyStatRepo.sumNet(record.getId()));
        // 计算手续费、服务费
        record.setCommission(dailyStatRepo.sumCommission(record.getId()));
        // 获取期末资产总值
        record.setFinalCapital(dailyStatRepo.getCapital(record.getId(), finishedDate));
        // 计算策略指数
        record.setAnnualizedReturns(calcAnnualizedReturns(record));
        record.setWinRatio(calcWinRatio(record.getId()));
        record.setSharpRatio(calcSharpRatio(record.getId(), record.getInitCapital()));
        record.setInformationRatio(calcInformationRatio(record.getId()));
        record.setSortinoRatio(calcSortinoRatio(record.getId(), record.getInitCapital()));
        // 计算最大回撤
        calcMaxDrawDown(record);
        return record;
    }

    /**
     * 计算年化收益
     * @return
     */
    public BigDecimal calcAnnualizedReturns(final BackTestRecord record) {
        // 年化收益计算公式： pow((finalCapital - initCapital), 1/days/365) - 1;
        // step1: 计算天数
        long diff = record.getEndDateTime().getTime() - record.getStartDateTime().getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        // step2: 计算指数
        double e = 1.0 / days / 365.0;
        // step3: 计算收益率
        BigDecimal returnValue = record.getFinalCapital().subtract(record.getInitCapital());
        if ( BigDecimal.ZERO.compareTo(returnValue) == 0 ) {
            return BigDecimal.ZERO;
        }
        BigDecimal returnRatio = record.getInitCapital().add(returnValue).divide(record.getInitCapital(), 4, RoundingMode.HALF_UP);
        double pow = Math.pow(returnRatio.doubleValue(), e);
        return BigDecimal.valueOf(pow - 1);
    }

    /**
     * 计算最大回撤、最大回撤周期
     * @param record 回测记录
     * @return
     */
    public void calcMaxDrawDown(final BackTestRecord record) {
        List<BackTestDailyStat> stats = dailyStatRepo.findAllByRecordId(record.getId());
        if ( CollectionUtils.isEmpty(stats) ) {
            record.setMaxDrawDown(BigDecimal.ZERO);
            record.setMaxDrawDownPeriod(0);
            return ;
        }

        BigDecimal peek = stats.get(0).getCapital();
        BigDecimal maxDrawDown = BigDecimal.ZERO;
        Date maxDate = null;
        Date minDate = null;
        for (BackTestDailyStat stat : stats) {
            if ( stat.getCapital().compareTo(peek) > 0 ) {
                peek = stat.getCapital();
                maxDate = stat.getDate();
            } else {
                BigDecimal drawdown = peek.subtract(stat.getCapital()).divide(peek, 4, RoundingMode.HALF_UP);
                if ( maxDrawDown.compareTo(drawdown) > 0 ) {
                    minDate = stat.getDate();
                }
            }
        }
        if ( maxDate == null || minDate == null ) {
            record.setMaxDrawDown(BigDecimal.ZERO);
            record.setMaxDrawDownPeriod(0);
        } else {
            record.setMaxDrawDown(maxDrawDown.multiply(new BigDecimal(100)));
            record.setMaxDrawDownPeriod(dailyStatRepo.countByRecordIdAndDateRange(record.getId(), minDate, maxDate));
        }
    }

    /**
     * 计算胜率
     * @param recordId
     * @return
     */
    public BigDecimal calcWinRatio(final Long recordId) {
        Integer winCount = closeRepo.countWinByRecordId(recordId);
        Integer totalCount = closeRepo.countTotalByRecordId(recordId);
        if ( totalCount <= 0 ) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(winCount)
                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算夏普比率
     * 夏普比率的结果表示每承担一单位风险，投资组合能够获得多少超额收益
     * @param recordId
     * @return
     */
    public BigDecimal calcSharpRatio(final Long recordId, final BigDecimal initCapital) {
        // step1: 按日统计收益率
        List<Tuple> returnRecords = dailyStatRepo.getReturnRateByRecordId(recordId, initCapital);
        // step1.1: 获取投资组合回报率
        List<BigDecimal> ratios = returnRecords.stream().map(r -> r.get("ratio", BigDecimal.class)).collect(Collectors.toList());
        // step1.2: 计算平均回报率
        double avgRatio = ratios.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0.000);
        // step2: 计算超额收益率
        List<BigDecimal> excessRatio = ratios.stream().map(r -> r.subtract(BackTestDailyStatService.BENCHMARK_RATE)).collect(Collectors.toList());
        // step3: 计算标准差
        BigDecimal standardDeviation = BigDecimal.valueOf(Math.sqrt(excessRatio.stream().mapToDouble(e -> Math.pow(e.doubleValue() - (avgRatio - BackTestDailyStatService.BENCHMARK_RATE.doubleValue()), 2))
                .average().orElse(0.000)));
        if ( BigDecimal.ZERO.compareTo(standardDeviation) == 0 ) {
            return BigDecimal.ZERO;
        }
        // 计算夏普比率
        return BigDecimal.valueOf(avgRatio).subtract(BackTestDailyStatService.BENCHMARK_RATE).divide(standardDeviation, 4, RoundingMode.HALF_UP);
    }

    /**
     * 计算信息比率
     * 投资组合经理每承担一单位跟踪误差所获得超额回报的数量
     * @param recordId
     * @return
     */
    public BigDecimal calcInformationRatio(final Long recordId) {
        // 信息比率 = (Rp - Rb) / σp
        List<BackTestDailyStat> stats = dailyStatRepo.findAllByRecordId(recordId);
        List<BigDecimal> benchmarks = stats.stream().map(BackTestDailyStat::getBenchmark).collect(Collectors.toList());
        List<BigDecimal> profits = stats.stream().map(BackTestDailyStat::getNet).collect(Collectors.toList());

        if ( CollectionUtils.isEmpty(profits) || CollectionUtils.isEmpty(benchmarks) ) {
            throw new LeekRuntimeException("[回测id: " +recordId+ "]回测收益数据为空，请检查程序");
        }

        // 计算超额回报
        List<BigDecimal> excessReturns = new ArrayList<>();
        BigDecimal sumExcessReturns = BigDecimal.ZERO;
        for (int i = 0; i < profits.size(); i++) {
            BigDecimal excessReturn = profits.get(i).subtract(benchmarks.get(i));
            excessReturns.add(excessReturn);
            sumExcessReturns = sumExcessReturns.add(excessReturn);
        }

        // 计算平均超额回报
        BigDecimal averageExcessReturn = sumExcessReturns.divide(BigDecimal.valueOf(excessReturns.size()), 4, RoundingMode.HALF_UP);

        // 计算方差
        BigDecimal sumSquaredDifferences = BigDecimal.ZERO;
        for (BigDecimal excessReturn : excessReturns) {
            sumSquaredDifferences = sumSquaredDifferences.add(excessReturn.subtract(averageExcessReturn).pow(2));
        }
        // 计算追踪误差
        BigDecimal trackingError = BigDecimal.valueOf(Math.sqrt((sumSquaredDifferences.divide(BigDecimal.valueOf(excessReturns.size() - 1), 4, RoundingMode.HALF_UP).doubleValue())));
        if ( BigDecimal.ZERO.compareTo(trackingError) == 0 ) {
            log.error("[回测id: {}]的trackingError为0", recordId);
            return BigDecimal.ZERO;
        }
        // 计算信息比率
        return averageExcessReturn.divide(trackingError, 4, RoundingMode.HALF_UP);
    }

    /**
     * 计算索诺提比率
     * 衡量投资组合风险调整收益的指标，它专注于下行风险，即负回报的风险，而不是整个回报分布的标准差
     * @param recordId
     * @return
     */
    public BigDecimal calcSortinoRatio(final Long recordId, final BigDecimal initCapital) {
        // step1: 按日统计收益率
        List<Tuple> returnRecords = dailyStatRepo.getReturnRateByRecordId(recordId, initCapital);
        // step1.1: 获取投资组合回报率
        List<BigDecimal> ratios = returnRecords.stream().map(r -> r.get("ratio", BigDecimal.class)).collect(Collectors.toList());
        // step1.2: 计算平均回报率
        double avgRatio = ratios.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0.000);
        // step2: 计算下行风险
        double sumValue = ratios.stream().mapToDouble(r -> Math.pow(Math.min(r.subtract(BackTestDailyStatService.BENCHMARK_RATE).doubleValue(), 0), 2))
                .sum();
        BigDecimal downsideStandardDeviation = BigDecimal.valueOf(Math.sqrt(sumValue / ratios.size()));
        if ( BigDecimal.ZERO.compareTo(downsideStandardDeviation) == 0 ) {
            return BigDecimal.ZERO;
        }
        // step3: 计算索诺提比率
        return BigDecimal.valueOf(avgRatio).subtract(BackTestDailyStatService.BENCHMARK_RATE).divide(downsideStandardDeviation, 4, RoundingMode.HALF_UP);
    }

}
