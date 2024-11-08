package club.cybecraftman.leek.domain.meta;

import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.repo.meta.repository.ICalendarRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class CalendarService {

    @Autowired
    private ICalendarRepo calendarRepo;

    /**
     * 根据CSV文件导入到交易日历表
     * @param filePath
     */
    @Transactional
    public void rebuild(final String filePath, final Market market, final FinanceType financeType) throws LeekException {
        if ( null == market ) {
            throw new LeekException("交易市场不可为空");
        }
        if ( null == financeType ) {
            throw new LeekException("金融产品不可为空");
        }
        // step1: 清除指定market和financeType的交易日历
        //calendarRepo.deleteAllByMarketAndFinanceType(market.getCode(), financeType.getType());
        // step2: TODO: 导入CSV文件
    }

}
