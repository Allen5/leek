package club.cybecraftman.leek.creeper.future.cn;

import club.cybecraftman.leek.common.constant.creep.DataType;
import club.cybecraftman.leek.common.constant.creep.SourceName;
import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.event.LeekEvent;
import club.cybecraftman.leek.common.event.etl.BarEvent;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.creeper.BaseCreeper;
import club.cybecraftman.leek.reader.future.DCEExcelReader;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@Slf4j
public class FutureBarDCECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void before() {
        // TODO:
    }

    @Override
    protected void after() {
        // TODO:

    }

    @Override
    protected void doCreep(final Page page) throws LeekException {
        // step0: 获取当前交易日
        String currentTradeDate = getCurrentTradeDate(sdf);
        // step1: 校验交易日
        checkTradeDate(page, currentTradeDate);
        // step2: 下载文件
        String filename = downloadFile(page, currentTradeDate);
        // step3: 解析excel文件
        List<FutureBarEventData> items;
        try {
            items = DCEExcelReader.readDailyBar(sdf.parse(currentTradeDate), filename);
        } catch (ParseException e) {
            throw new LeekRuntimeException("日期解析失败: " + currentTradeDate);
        }
        // step4: 发送消息
        BarEvent<FutureBarEventData> event = new BarEvent<>();
        event.setBarType(BarType.DAILY.getType());
        event.setMarketCode(event.getMarketCode());
        event.setFinanceType(event.getFinanceType());
        event.setItems(items);
        getKafkaProducer().publish(LeekEvent.ON_BAR_RECEIVED.topic, event);
    }

    private void checkTradeDate(final Page page, final String currentTradeDate) throws LeekException {
        // step1: 获取页面上的交易日信息
        // meta: div.jysjtop > div.fr > span
        ElementHandle el = page.querySelector("div.tradeResult02 > p > span");
        if ( el == null ) {
            log.error("[DCE]获取地址: {} 上的元素[div.tradeResult02 > p > span] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: div.tradeResult02 > p > span");
        }
        String content = el.innerText().trim();
        if ( !StringUtils.hasText(content) ) {
            log.error("[DCE]地址: {}上的元素[div.tradeResult02 > p > span]内容为空. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容为空: div.tradeResult02 > p > span");
        }
        String[] items = content.split(" ");
        String candidate = items[0].split(":")[1].trim();
        if ( !currentTradeDate.equals(candidate) ) {
            log.error("[DCE]当前的交易日为:{}，官方当前数据所属交易日为: {}。两者不一致，等待官方更新.", currentTradeDate, candidate);
            throw new LeekException("数据所属交易日和当前交易日不匹配");
        }
    }

    /**
     * 下载excel文件
     * @param page
     */
    private String downloadFile(final Page page, final String currentTradeDate) {
        String filepath = DOWNLOAD_FILE_ROOT_DIR + File.separator + "DCE_" + currentTradeDate + ".xls";
        List<ElementHandle> els = page.querySelectorAll("div.tradeResult02 > ul > li > a");
        if ( null == els || els.size() != 3) {
            log.error("[DCE]获取地址: {} 上的元素[div.tradeResult02 > ul > li > a] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: div.tradeResult02 > ul > li > a");
        }
        log.info("[DCE]开始下载文件");
        Download download = page.waitForDownload(els.get(1)::click); // 中间那个是下载excel的
        download.saveAs(Paths.get(filepath));
        log.info("[DCE]文件下载完成。 保存位置: {}", filepath);
        return filepath;
    }

    @Override
    public boolean isSupport(CreepEvent event) {
        // 中国期货市场大商所官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.DCE.getName().equals(event.getSourceName());
    }

}
