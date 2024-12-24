package club.cybercraftman.leek.core.eveluator;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestDailyStatRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    // daily里面已经将持仓收益计算完了，直接统计即可
    @Autowired
    private IBackTestDailyStatRepo dailyStatRepo;

    public BackTestRecord evaluate(final BackTestRecord record, final Date finishedDate) {
        // 计算策略收益（包含已结算收益 + 持仓收益）
        record.setProfit(calcTotalProfit(market, financeType,record.getId(), finishedDate));
        // 计算净收益（包含已结算净收益 + 持仓收益）
        record.setNet(calcTotalNet(market, financeType, record.getId(), finishedDate));
        // 计算期末资产总值
        record.setFinalCapital(calcFinalCapital(market, financeType, record, finishedDate));
        record.setAnnualizedReturns(calcAnnualizedReturns(record));
        record.setMaxDrawDown(calcMaxDrawDown(record.getId()));
        record.setMaxDrawDownPeriod(calcMaxDrawDownPeriod(record.getId()));
        record.setWinRatio(calcWinRatio(record.getId()));
        record.setSharpRatio(calcSharpRatio(record.getId()));
        record.setInformationRatio(calcInformationRatio(record.getId()));
        record.setSortinoRatio(calcSortinoRatio(record.getId()));
        return record;
    }

    private BigDecimal calcTotalProfit(final Market market, final FinanceType financeType, final Long recordId, final Date finishedDate) {
        // 已结算收益
        BigDecimal settledProfit = profitRepo.sumSettledProfitByRecordId(recordId);
        // 持仓收益
        BigDecimal positionProfit = calcPositionProfit(market, financeType, recordId, finishedDate);
        return settledProfit.add(positionProfit);
    }

    /**
     * 计算总净收益
     * @param market
     * @param financeType
     * @param recordId
     * @param finishedDate
     * @return
     */
    private BigDecimal calcTotalNet(final Market market, final FinanceType financeType, final Long recordId, final Date finishedDate, final Broker broker) {
        // 已结算净收益
        BigDecimal settledProfit = profitRepo.sumNetByRecordId(recordId);
        // 持仓收益
        BigDecimal positionProfit = calcPositionNet(market, financeType, recordId, finishedDate, broker);
        return settledProfit.add(positionProfit);
    }

    private BigDecimal calcPositionProfit(final Market market, final FinanceType financeType, final Long recordId, final Date currentDate) {
        // step1: 获取持仓信息
        List<BackTestPosition> positions = positionRepo.findAllByRecordIdAndStatus(recordId, PositionStatus.OPEN.getStatus());
        if (CollectionUtils.isEmpty(positions)) {
            return BigDecimal.ZERO;
        }
        // step2: 逐个持仓计算其最后回测bar的持仓收益
        BigDecimal totalProfit = BigDecimal.ZERO;
        for (BackTestPosition position: positions) {
            CommonBar bar = backTestDataRepo.getCurrentBar(market, financeType, currentDate, position.getSymbol());
            if (bar == null) {
                log.error("获取回测bar失败，market: {}, financeType: {}, symbol: {}", market, financeType, position.getSymbol());
                throw new LeekRuntimeException("获取回测bar失败. datetime: " + currentDate + ", symbol: " + position.getSymbol());
            }
            BigDecimal diff;
            if ( Direction.LONG.getType().equals(position.getDirection()) ) {
                diff = bar.getSettle().subtract(position.getOpenPrice());
            } else {
                diff = position.getOpenPrice().subtract(bar.getSettle());
            }
            BigDecimal profit = diff.multiply(BigDecimal.valueOf(position.getAvailableVolume())).multiply(bar.getMultiplier()).multiply(bar.getPriceTick());
            totalProfit = totalProfit.add(profit);
        }
        return totalProfit;
    }

    private BigDecimal calcPositionNet(final Market market, final FinanceType financeType, final Long recordId, final Date currentDate, final Broker broker) {
        // step1: 获取持仓信息
        List<BackTestPosition> positions = positionRepo.findAllByRecordIdAndStatus(recordId, PositionStatus.OPEN.getStatus());
        if (CollectionUtils.isEmpty(positions)) {
            return BigDecimal.ZERO;
        }
        // step2: 逐个持仓计算其最后回测bar的持仓收益
        BigDecimal totalNet = BigDecimal.ZERO;
        for (BackTestPosition position: positions) {
            CommonBar bar = backTestDataRepo.getCurrentBar(market, financeType, currentDate, position.getSymbol());
            if (bar == null) {
                log.error("获取回测bar失败，market: {}, financeType: {}, symbol: {}", market, financeType, position.getSymbol());
                throw new LeekRuntimeException("获取回测bar失败. datetime: " + currentDate + ", symbol: " + position.getSymbol());
            }
            BigDecimal diff;
            if ( Direction.LONG.getType().equals(position.getDirection()) ) {
                diff = bar.getSettle().subtract(position.getOpenPrice());
            } else {
                diff = position.getOpenPrice().subtract(bar.getSettle());
            }
            // TODO: 这里会有问题，commission需要记录下来。当合约乘数调整之后，两次算的手续费不一致。
            BigDecimal profit = diff.multiply(BigDecimal.valueOf(position.getAvailableVolume())).multiply(bar.getMultiplier()).multiply(bar.getPriceTick());
            BigDecimal commission = broker.getCommission(position.getOpenPrice(), position.getAvailableVolume(), bar.getMultiplier(), bar.getPriceTick());
            BigDecimal net = profit.subtract(commission);
            totalNet = totalNet.add(net);
        }
        return totalNet;
    }




    /**
     * 计算期末资产
     * @return
     */
    public BigDecimal calcFinalCapital(final Market market, final FinanceType financeType, final BackTestRecord record, final Date finishedDateTime) {
        // step1: 获取持仓信息
        List<BackTestPosition> positions = positionRepo.findAllByRecordIdAndStatus(record.getId(), PositionStatus.OPEN.getStatus());
        if (CollectionUtils.isEmpty(positions)) {
            return record.getFinalCapital();
        }
        // step2: 逐个持仓计算其最后回测bar的持仓收益
        BigDecimal totalProfit = BigDecimal.ZERO;
        for (BackTestPosition position: positions) {
            CommonBar bar = backTestDataRepo.getCurrentBar(market, financeType, finishedDateTime, position.getSymbol());
            if (bar == null) {
                log.error("获取回测bar失败，market: {}, financeType: {}, symbol: {}", market, financeType, position.getSymbol());
                throw new LeekRuntimeException("获取回测bar失败. datetime: " + finishedDateTime + ", symbol: " + position.getSymbol());
            }
            BigDecimal diff;
            if ( Direction.LONG.getType().equals(position.getDirection()) ) {
                diff = bar.getSettle().subtract(position.getOpenPrice());
            } else {
                diff = position.getOpenPrice().subtract(bar.getSettle());
            }
            BigDecimal profit = diff.multiply(BigDecimal.valueOf(position.getAvailableVolume())).multiply(bar.getMultiplier()).multiply(bar.getPriceTick());
            totalProfit = totalProfit.add(profit);
        }
        // finalCapital + 持仓资产价值（期货按当日结算价计算，股票按当日收盘价计算) + 持仓保证金
        return record.getFinalCapital().add(totalProfit);
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
     * 计算最大回撤
     * @param recordId
     * @return
     */
    public BigDecimal calcMaxDrawDown(final Long recordId) {
        // TODO: 待思考
        return BigDecimal.ZERO;
    }

    /**
     * 计算最大回撤周期
     * @param recordId
     * @return
     */
    public Integer calcMaxDrawDownPeriod(final Long recordId) {
        // TODO: 待思考
        return 0;
    }

    /**
     * 计算胜率
     * @param recordId
     * @return
     */
    public BigDecimal calcWinRatio(final Long recordId) {
        Integer winCount = profitRepo.countWinByRecordId(recordId);
        Integer loseCount = profitRepo.countLoseByRecordId(recordId);
        if (winCount + loseCount > 0) {
            return BigDecimal.valueOf(winCount).divide(BigDecimal.valueOf(winCount + loseCount), 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算夏普比率
     * @param recordId
     * @return
     */
    public BigDecimal calcSharpRatio(final Long recordId) {
        // Rp 是投资组合的预期回报率。
        // Rf 是无风险利率，通常可以用短期政府债券的利率作为代理。
        // σp 是投资组合的标准差，代表投资的波动性或风险。
        // sharp = (Rp-Rf) / σp
        return BigDecimal.ZERO;
    }

    /**
     * 计算信息比率
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
     * @param recordId
     * @return
     */
    public BigDecimal calcSortinoRatio(final Long recordId) {

        return BigDecimal.ZERO;
    }

}
