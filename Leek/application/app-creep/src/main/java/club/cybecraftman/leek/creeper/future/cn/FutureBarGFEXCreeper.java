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
import club.cybecraftman.leek.reader.future.GFEXExcelReader;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FutureBarGFEXCreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


    @Override
    protected void doCreep(Page page) throws LeekException {
        // step0: 获取当前交易日
        String currentTradeDate = getCurrentTradeDate(sdf);
        // step1: 校验交易日
        checkTradeDate(page, currentTradeDate);
        // step2: 下载文件
        String filename = downloadFile(page, currentTradeDate);
        // step3: 解析excel文件
        List<FutureBarEventData> items;
        try {
            items = GFEXExcelReader.readDailyBar(sdf.parse(currentTradeDate), filename);
        } catch (ParseException e) {
            throw new LeekRuntimeException("日期解析失败: " + currentTradeDate);
        }
        // step4: 发送消息
        this.publishBars(BarType.DAILY, items);

    }

    private void checkTradeDate(final Page locator, final String currentTradeDate) throws LeekException {
        // step1: 获取页面上的交易日信息
        // meta: div.mainbox > div.remark
        Locator el = locator.locator("div.mainbox > div.remark");
        if ( el == null ) {
            log.error("[GFEX]获取地址: {} 上的元素[div.mainbox > div.remark] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: div.mainbox > div.remark");
        }
        String content = el.innerText().trim();
        if ( !StringUtils.hasText(content) ) {
            log.error("[GFEX]地址: {}上的元素[div.mainbox > div.remark]内容为空. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容为空: div.mainbox > div.remark");
        }
        Pattern pattern = Pattern.compile("\\d{4}\\d{2}\\d{2}");
        Matcher matcher = pattern.matcher(content);
        if ( !matcher.find() ) {
            log.error("[GFEX]地址: {}上的元素[div.mainbox > div.remark]内容不包含日期. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容不包含日期: div.mainbox > div.remark");
        }
        String candidate = matcher.group(0);
        if ( !currentTradeDate.equals(candidate) ) {
            log.error("[GFEX]当前的交易日为:{}，官方当前数据所属交易日为: {}。两者不一致，等待官方更新.", currentTradeDate, candidate);
            throw new LeekException("数据所属交易日和当前交易日不匹配");
        }
    }

    /**
     * 下载excel文件
     * @param locator
     */
    private String downloadFile(final Page locator, final String currentTradeDate) {
        String filepath = DOWNLOAD_FILE_ROOT_DIR + File.separator + "GFEX_" + currentTradeDate + ".xls";
        Locator el = locator.locator("button#export_excel");
        if ( null == el ) {
            log.error("[GFEX]获取地址: {} 上的元素[button#export_excel] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: button#export_excel");
        }
        log.info("[GFEX]开始下载文件");
        Download download = locator.waitForDownload(el::click);
        download.saveAs(Paths.get(filepath));
        log.info("[GFEX]文件下载完成。 保存位置: {}", filepath);
        return filepath;
    }

    @Override
    public boolean isSupport(CreepEvent event) {
        // 中国期货市场广期所官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.GFEX.getName().equals(event.getSourceName());

    }
}
