package club.cybercraftman.leek.repo.financedata;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.repo.financedata.model.future.FutureBackTest;
import club.cybercraftman.leek.repo.financedata.repository.IFutureBackTestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回测数据操作
 */
@Component
@Slf4j
public class BackTestDataRepo {

    @Autowired
    private IFutureBackTestRepo futureBackTestRepo;

    /**
     * 获取指定的开盘价
     * @param market
     * @param financeType
     * @param date
     * @param symbol
     * @return
     */
    public BigDecimal getOpenPrice(final Market market, final FinanceType financeType, final Date date, final String symbol) {
        if ( Market.CN.equals(market) && FinanceType.FUTURE.equals(financeType) ) {
            FutureBackTest item = futureBackTestRepo.findOneByDateAndSymbol(date, symbol);
            if ( null == item ) {
                throw new LeekRuntimeException("不存在symbol: " + symbol + "对应日期时间: " + date + "的行情数据");
            }
            return item.getOpen();
        }
        throw new LeekRuntimeException("不支持的交易市场: " +market+ "和金融产品: " + financeType);
    }

}
