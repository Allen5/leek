package club.cybercraftman.leek.repo.financedata;

import club.cybercraftman.leek.common.bean.CommonBar;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.repo.financedata.model.future.FutureBackTest;
import club.cybercraftman.leek.repo.financedata.repository.ICalendarRepo;
import club.cybercraftman.leek.repo.financedata.repository.IFutureBackTestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回测数据操作
 */
@Component
@Slf4j
public class BackTestDataRepo {

    @Autowired
    private IFutureBackTestRepo futureBackTestRepo;

    @Autowired
    private ICalendarRepo calendarRepo;

    /**
     * 获取指定的开盘价
     * @param market
     * @param financeType
     * @param date
     * @param symbol
     * @return
     */
    public CommonBar getCurrentBar(final Market market, final FinanceType financeType, final Date date, final String symbol) {
        if ( Market.CN.equals(market) && FinanceType.FUTURE.equals(financeType) ) {
            FutureBackTest item = futureBackTestRepo.findOneByDateAndSymbol(date, symbol);
            if ( null == item ) {
                throw new LeekRuntimeException("不存在symbol: " + symbol + "对应日期时间: " + date + "的行情数据");
            }
            CommonBar bar = CommonBar.builder().build();
            BeanUtils.copyProperties(item, bar);
            return bar;
        }
        throw new LeekRuntimeException("不支持的交易市场: " +market+ "和金融产品: " + financeType);
    }

    /**
     * 获取交易代码
     * @param market
     * @param financeType
     * @param code
     * @param datetime
     * @return
     */
    public CommonBar getMainCurrentBar(final Market market, final FinanceType financeType, final String code, final Date datetime) {
        // 非期货市场，code即交易代码
        FutureBackTest item;
        if ( !FinanceType.FUTURE.equals(financeType) ) {
            item = futureBackTestRepo.findOneByDateAndSymbol(datetime, code);
        } else {
            // 期货市场获取当前的主力合约作为交易代码
            // Tips: 此处是否需要可配置化？
            item = futureBackTestRepo.findMainByDateAndCode(code, datetime);
        }
        if ( null == item ) {
            throw new LeekRuntimeException("当前品种:" + code + "在日期:" + datetime + "不存在行情数据，请检查数据");
        }
        CommonBar bar = CommonBar.builder().build();
        BeanUtils.copyProperties(item, bar);
        return bar;
    }

    /**
     * 获取交易标的前N个周期的行情数据
     * @param market 交易市场
     * @param financeType 金融产品
     * @param datetime 截止周期时间
     * @param symbol 交易代码
     * @param period 周期
     * @return
     */
    public List<CommonBar> getPeriodBars(final Market market, final FinanceType financeType, final Date datetime, final String symbol, final Integer period) {
        if ( !Market.CN.equals(market) && !FinanceType.FUTURE.equals(financeType) ) {
            // TODO: 其他市场暂时不支持
            return null;
        }
        if ( null == datetime ) {
            return null;
        }
        Date minDate = calendarRepo.findMinDate(market.getCode(), financeType.getType(), datetime, period);
        List<FutureBackTest> items = futureBackTestRepo.findAllBySymbolAndDateRange(symbol, minDate, datetime);
        return items.stream().map(i -> {
            CommonBar bar = CommonBar.builder().build();
            BeanUtils.copyProperties(i, bar);
            return bar;
        }).collect(Collectors.toList());
    }

}
