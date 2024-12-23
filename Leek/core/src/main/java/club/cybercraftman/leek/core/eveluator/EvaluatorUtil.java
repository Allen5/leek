package club.cybercraftman.leek.core.eveluator;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.Direction;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.repo.financedata.BackTestDataRepo;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestPosition;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestRecord;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestProfitRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 评估工具
 */
@Component
@Slf4j
public class EvaluatorUtil {

    @Autowired
    private IBackTestPositionRepo positionRepo;

    @Autowired
    private BackTestDataRepo backTestDataRepo;

    @Autowired
    private IBackTestProfitRepo profitRepo;

    public BackTestRecord evaluate(final Market market, final FinanceType financeType, final BackTestRecord record, final Date finishedDate) {
        // 获取资产价值
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
        // step2: 逐个持仓计算其最后回测bar的资产价值
        BigDecimal asset = BigDecimal.ZERO;
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
            BigDecimal posAsset = diff.multiply(BigDecimal.valueOf(position.getAvailableVolume())).multiply(bar.getMultiplier()).multiply(bar.getPriceTick());
            asset = asset.add(posAsset);
        }
        // finalCapital + 持仓资产价值（期货按当日结算价计算，股票按当日收盘价计算）
        return record.getFinalCapital().add(asset);
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
        double pow = Math.pow(record.getFinalCapital().subtract(record.getInitCapital()).doubleValue(), e);
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

        return BigDecimal.ZERO;
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
