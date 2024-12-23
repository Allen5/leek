package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.core.broker.Broker;
import club.cybercraftman.leek.repo.financedata.BackTestDataRepo;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestDailyStat;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestProfit;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestDailyStatRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestProfitRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestRecordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private IBackTestPositionRepo positionRepo;

    @Autowired
    private IBackTestProfitRepo profitRepo;

    @Autowired
    private BackTestDataRepo backTestDataRepo;

    /**
     * 统计当日的回测数据
     * @param market
     * @param financeType
     * @param recordId
     * @param currentDate
     */
    @Transactional
    public void statDaily(final Market market, final FinanceType financeType, final Long recordId, final Date currentDate, final Broker broker) {
        // step1: 获取回测记录
        Optional<BackTestRecord> op = recordRepo.findById(recordId);
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不存在对应的回测记录: " + recordId);
        }
        // 获取当前持仓单
        List<BackTestPosition> openPositions = positionRepo.findAllByRecordIdAndStatus(recordId, PositionStatus.OPEN.getStatus());
        // 获取当前收益数据
        List<BackTestProfit> profits = profitRepo.findAllByRecordIdAndClosedAt(recordId, currentDate);
        BackTestRecord record = op.get();
        BackTestDailyStat stat = new BackTestDailyStat();
        stat.setRecordId(recordId);
        stat.setDate(currentDate);
        stat.setBenchmark(calcBenchmark(record.getInitCapital()));
        // 计算当日收益: 当日平仓单的收益 + 当日持仓单的收益
        stat.setProfit(calcDailyProfit(market, financeType, profits, openPositions, currentDate));
        // 计算当日手续费服务费: 当日开、平仓单的手续费服务费
        stat.setCommission(calcCommission(market, financeType, profits, openPositions, currentDate, broker));
        // 计算当日净收益： 当日收益 - 当日的手续费服务费
        stat.setNet(stat.getProfit().subtract(stat.getCommission()));
        // 计算当日资产总值： 当日净收益 + 当前剩余资金 + 当日已支出保证金
        stat.setCapital(stat.getNet().add(broker.getCapital()).add(calcDeposit(market, financeType, openPositions, currentDate, broker)));
        dailyStatRepo.save(stat);
    }

    /**
     * Tips: 此处忽略复利计算，仅考虑基准收益
     * @param initCapital
     * @return
     */
    private BigDecimal calcBenchmark(final BigDecimal initCapital) {
        // 折算为日收益
        return initCapital.multiply(BENCHMARK_RATE.divide(BigDecimal.valueOf(360), 6, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * 计算当日收益
     * @param profits
     * @param openPositions
     * @return
     */
    private BigDecimal calcDailyProfit(final Market market, final FinanceType financeType, final List<BackTestProfit> profits, final List<BackTestPosition> openPositions, final Date currentDate) {
        BigDecimal profit = BigDecimal.ZERO;
        for (BackTestProfit p: profits) {
            profit = profit.add(p.getProfit());
        }
        for (BackTestPosition p: openPositions) {
            // 获取当前bar的价格
            CommonBar bar = backTestDataRepo.getCurrentBar(market, financeType, currentDate, p.getSymbol());
            if (bar == null) {
                log.error("获取回测bar失败，market: {}, financeType: {}, symbol: {}", market, financeType, p.getSymbol());
                throw new LeekRuntimeException("获取回测bar失败. datetime: " + currentDate + ", symbol: " + p.getSymbol());
            }
            // 计算当前持仓的收益
            BigDecimal diff;
            if ( Direction.LONG.getType().equals(p.getDirection()) ) {
                diff = bar.getSettle().subtract(p.getOpenPrice());
            } else {
                diff = p.getOpenPrice().subtract(bar.getSettle());
            }
            BigDecimal posAsset = diff.multiply(BigDecimal.valueOf(p.getAvailableVolume())).multiply(bar.getMultiplier()).multiply(bar.getPriceTick());
            profit = profit.add(posAsset);
        }
        return profit;
    }

    /**
     * 计算当日手续费、服务费
     * 当日开仓的手续费 + 当日平仓的手续费、服务费
     * @return
     */
    private BigDecimal calcCommission(final Market market, final FinanceType financeType, final List<BackTestProfit> profits, final List<BackTestPosition> openPositions, final Date currentDate, final Broker broker) {
        BigDecimal commission = BigDecimal.ZERO;
        // 计算当日开仓的手续费
        List<BackTestPosition> todayOpenPositions = openPositions.stream()
                .filter(p -> p.getCreatedAt().compareTo(currentDate) == 0).collect(Collectors.toList());
        for (BackTestPosition position : todayOpenPositions) {
            CommonBar bar = backTestDataRepo.getCurrentBar(market, financeType, currentDate, position.getSymbol());
            if (bar == null) {
                log.error("获取回测bar失败，market: {}, financeType: {}, symbol: {}", market, financeType, position.getSymbol());
                throw new LeekRuntimeException("获取回测bar失败. datetime: " + currentDate + ", symbol: " + position.getSymbol());
            }
            commission = commission.add(broker.getCommission(position.getOpenPrice(), position.getAvailableVolume(), bar.getMultiplier()));
        }
        // 计算当日平仓的手续费、服务费
        for (BackTestProfit profit : profits) {
            commission = commission.add(profit.getCloseCommission()).add(profit.getOtherCommission());
        }
        return commission;
    }

    /**
     * 计算当日支出的保证金
     * @param market
     * @param financeType
     * @param openPositions
     * @param currentDate
     * @param broker
     * @return
     */
    private BigDecimal calcDeposit(final Market market, final FinanceType financeType, final List<BackTestPosition> openPositions, final Date currentDate, final Broker broker) {
        BigDecimal deposit = BigDecimal.ZERO;
        // 计算当日开仓支出的保证金
        List<BackTestPosition> todayOpenPositions = openPositions.stream()
                .filter(p -> p.getCreatedAt().compareTo(currentDate) == 0).collect(Collectors.toList());
        for (BackTestPosition position : todayOpenPositions) {
            CommonBar bar = backTestDataRepo.getCurrentBar(market, financeType, currentDate, position.getSymbol());
            if (bar == null) {
                log.error("获取回测bar失败，market: {}, financeType: {}, symbol: {}", market, financeType, position.getSymbol());
                throw new LeekRuntimeException("获取回测bar失败. datetime: " + currentDate + ", symbol: " + position.getSymbol());
            }
            deposit = deposit.add(broker.getDepositValue(position.getOpenPrice(), position.getAvailableVolume(), bar.getMultiplier()));
        }
        return deposit;
    }

}
