package club.cybecraftman.leek.creeper.future.cn;

import club.cybecraftman.leek.common.constant.creep.DataType;
import club.cybecraftman.leek.common.constant.creep.SourceName;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.creeper.BaseCreeper;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class FutureBarSinaCreeper extends BaseCreeper {

    @Override
    protected void before() {

    }

    @Override
    protected void after() {

    }

    @Override
    protected void doCreep(final Page page) {
        // TODO: 执行爬取逻辑
    }

    @Override
    public boolean isSupport(CreepEvent event) {
        if ( !Market.CN.getCode().equals(event.getMarketCode()) ) {
            return false;
        }
        if ( !FinanceType.FUTURE.getType().equals(event.getFinanceType()) ) {
            return false;
        }
        if ( !DataType.BAR.getType().equals(event.getDataType()) ) {
            return false;
        }
        return SourceName.SINA.getName().equals(event.getSourceName());
    }
}
