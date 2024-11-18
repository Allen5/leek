package club.cybecraftman.leek.creeper.future.cn;

import club.cybecraftman.leek.common.constant.creep.DataType;
import club.cybecraftman.leek.common.constant.creep.SourceName;
import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.creeper.BaseCreeper;
import club.cybecraftman.leek.reader.future.CFFEXExcelReader;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class FutureBarCFFEXCreeper extends BaseCreeper {

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

        // step1: 下载文件
        // 获取元素: div.iffj > h1 > a
        ElementHandle el = page.querySelector("div.iffj > h1 > a");
        if ( el == null ) {
            log.error("[CFFEX]地址: {} 页面元素[div.iffj > h1 > a] 定位失败.", getEvent().getSource());
            throw new LeekException("页面元素[div.iffj > h1 > a]定位失败");
        }
        String currentTradeDate = getCurrentTradeDate(sdf);
        String filepath = DOWNLOAD_FILE_ROOT_DIR + File.separator + "CFFEX_" + currentTradeDate + ".csv";
        Download download = page.waitForDownload(el::click);
        download.saveAs(Paths.get(filepath));
        log.info("[CFFEX]文件下载完成。 保存位置: {}", filepath);

        // step2: 读取数据，发送消息
        List<FutureBarEventData> items;
        try {
            items = CFFEXExcelReader.readDailyBar(sdf.parse(currentTradeDate), filepath);
        } catch (ParseException e) {
            throw new LeekRuntimeException("日期解析失败: " + currentTradeDate);
        }
        // step4: 发送消息
        this.publishBars(BarType.DAILY, items);

    }

    @Override
    public boolean isSupport(CreepEvent event) {
        // 中国期货市场中金所官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.CFFEX.getName().equals(event.getSourceName());
    }

}
