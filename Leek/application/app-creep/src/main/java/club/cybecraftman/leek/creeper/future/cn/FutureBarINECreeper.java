package club.cybecraftman.leek.creeper.future.cn;

import club.cybecraftman.leek.common.constant.creep.DataType;
import club.cybecraftman.leek.common.constant.creep.SourceName;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.creeper.BaseCreeper;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class FutureBarINECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected boolean isRightTime() {
        // 校验今日是否为交易日
        String currentTradeDate = getCurrentTradeDate(sdf);
        String currentDate = sdf.format(new Date());
        if ( !currentDate.equals(currentTradeDate) ) {
            log.error("当前日期:[{}]非交易日. 当前的交易日为: [{}]", currentDate, currentTradeDate);
            return false;
        }
        return true;
    }

    @Override
    protected void doCreep(Page page) throws LeekException {

    }

    @Override
    public boolean isSupport(CreepEvent event) {
        // 中国期货市场上期能源官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.INE.getName().equals(event.getSourceName());
    }

}
