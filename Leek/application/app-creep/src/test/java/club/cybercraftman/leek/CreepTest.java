package club.cybercraftman.leek;

import club.cybecraftman.leek.CreepApplication;
import club.cybecraftman.leek.common.constant.creep.DataType;
import club.cybecraftman.leek.common.constant.creep.SourceName;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.creeper.BaseCreeper;
import club.cybecraftman.leek.creeper.CreeperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CreepApplication.class)
@Slf4j
public class CreepTest {

    @Autowired
    private CreeperBuilder builder;

    @Test
    public void testCZCECreep() {
        CreepEvent event = new CreepEvent();
        event.setMarketCode(Market.CN.getCode());
        event.setFinanceType(FinanceType.FUTURE.getType());
        event.setDataType(DataType.BAR.getType());
        event.setSourceName(SourceName.CZCE.getName());
        event.setSource("http://www.czce.com.cn/cn/jysj/mrhq/H770301index_1.htm");
        BaseCreeper creeper = builder.build(event);
        creeper.setEvent(event);
        creeper.creep();
    }

    @Test
    public void testDCECreep() {
        CreepEvent event = new CreepEvent();
        event.setMarketCode(Market.CN.getCode());
        event.setFinanceType(FinanceType.FUTURE.getType());
        event.setDataType(DataType.BAR.getType());
        event.setSourceName(SourceName.DCE.getName());
        event.setSource("http://www.dce.com.cn/dalianshangpin/xqsj/tjsj26/rtj/rxq/index.html");
        BaseCreeper creeper = builder.build(event);
        creeper.setEvent(event);
        creeper.creep();
    }

}
