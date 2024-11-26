package club.cybercraftman.leek;

import club.cybercraftman.leek.common.constant.creep.DataType;
import club.cybercraftman.leek.common.constant.creep.SourceName;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.event.creep.CreepEvent;
import club.cybercraftman.leek.creeper.BaseCreeper;
import club.cybercraftman.leek.creeper.CreeperBuilder;
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

    @Test
    public void testGFEXCreep() {
        // TODO: 元素内容为空: div.mainbox > div.remark
        CreepEvent event = new CreepEvent();
        event.setMarketCode(Market.CN.getCode());
        event.setFinanceType(FinanceType.FUTURE.getType());
        event.setDataType(DataType.BAR.getType());
        event.setSourceName(SourceName.GFEX.getName());
        event.setSource("http://www.gfex.com.cn/gfex/rihq/hqsj_tjsj.shtml");
        BaseCreeper creeper = builder.build(event);
        creeper.setEvent(event);
        creeper.creep();
    }

    @Test
    public void testCFFEXCreep() {
        CreepEvent event = new CreepEvent();
        event.setMarketCode(Market.CN.getCode());
        event.setFinanceType(FinanceType.FUTURE.getType());
        event.setDataType(DataType.BAR.getType());
        event.setSourceName(SourceName.CFFEX.getName());
        event.setSource("http://www.cffex.com.cn/rtj/");
        BaseCreeper creeper = builder.build(event);
        creeper.setEvent(event);
        creeper.creep();
    }

    @Test
    public void testSHFECreep() {
        CreepEvent event = new CreepEvent();
        event.setMarketCode(Market.CN.getCode());
        event.setFinanceType(FinanceType.FUTURE.getType());
        event.setDataType(DataType.BAR.getType());
        event.setSourceName(SourceName.SHFE.getName());
        event.setSource("https://www.shfe.com.cn/reports/tradedata/dailyandweeklydata/");
        BaseCreeper creeper = builder.build(event);
        creeper.setEvent(event);
        creeper.creep();
    }

    @Test
    public void testINECreep() {
        CreepEvent event = new CreepEvent();
        event.setMarketCode(Market.CN.getCode());
        event.setFinanceType(FinanceType.FUTURE.getType());
        event.setDataType(DataType.BAR.getType());
        event.setSourceName(SourceName.INE.getName());
        event.setSource("https://www.ine.cn/statements/daily/?paramid=kx");
        BaseCreeper creeper = builder.build(event);
        creeper.setEvent(event);
        creeper.creep();
    }

}
