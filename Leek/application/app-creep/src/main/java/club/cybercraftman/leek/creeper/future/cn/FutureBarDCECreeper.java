package club.cybercraftman.leek.creeper.future.cn;

import club.cybercraftman.leek.common.constant.creep.DataType;
import club.cybercraftman.leek.common.constant.creep.SourceName;
import club.cybercraftman.leek.common.constant.finance.BarType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.finance.future.Exchange;
import club.cybercraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybercraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.creeper.BaseCreeper;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FutureBarDCECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void doCreep(final Page page) throws LeekException {
        FrameLocator locator = page.frameLocator("iframe").last();
        // step0: 获取当前交易日
        String currentTradeDate = getCurrentTradeDate(sdf);
        // step1: 校验交易日
        checkTradeDate(locator, currentTradeDate);
        // step2: 解析页面内容，抓取数据
        this.publishBars(BarType.DAILY, buildItems(locator, getCurrentTradeDate()));
    }

    private void checkTradeDate(final FrameLocator locator, final String currentTradeDate) throws LeekException {
        // step1: 获取页面上的交易日信息
        // meta: div.jysjtop > div.fr > span
        Locator el = locator.locator("div.tradeResult02 > p > span");
        if ( el == null ) {
            log.error("[DCE]获取地址: {} 上的元素[div.tradeResult02 > p > span] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: div.tradeResult02 > p > span");
        }
        String content = el.innerText().trim();
        if ( !StringUtils.hasText(content) ) {
            log.error("[DCE]地址: {}上的元素[div.tradeResult02 > p > span]内容为空. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容为空: div.tradeResult02 > p > span");
        }

        Pattern pattern = Pattern.compile("\\d{4}\\d{2}\\d{2}");
        Matcher matcher = pattern.matcher(content);
        if ( !matcher.find() ) {
            log.error("[DCE]地址: {}上的元素[div.tradeResult02 > p > span]内容不包含日期. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容不包含日期: div.tradeResult02 > p > span");
        }
        String candidate = matcher.group(0);
        if ( !currentTradeDate.equals(candidate) ) {
            log.error("[DCE]当前的交易日为:{}，官方当前数据所属交易日为: {}。两者不一致，等待官方更新.", currentTradeDate, candidate);
            throw new LeekException("数据所属交易日和当前交易日不匹配");
        }
    }

    private List<FutureBarEventData> buildItems(final FrameLocator locator, final Date currentTradeDate) {
        // div.dataWrapper > div.dataArea > table > tbody > tr
        List<Locator> lines = locator.locator("div.dataWrapper > div.dataArea > table > tbody > tr").all();
        List<FutureBarEventData> items = new ArrayList<>();
        for(int i=1; i<lines.size(); i++) {
            List<Locator> cells = lines.get(i).locator("td").all();
            String name = cells.get(0).innerText().trim();
            if ( name.contains("小计") || name.contains("总计") ) {
                log.warn("[DCE]该行包含小计或总计，忽略. name: {}. line: {}", name, lines.get(i));
                continue;
            }
            FutureBarEventData data = new FutureBarEventData();
            data.setDatetime(currentTradeDate);
            data.setContractCode(cells.get(1).innerText().trim() + "." + Exchange.DCE.getCode()); // 合约代码
            data.setProductCode(extractProductCode(data.getContractCode()));
            data.setSymbol(data.getContractCode());
            data.setOpen(getValue(cells.get(2))); // 开盘价
            data.setHigh(getValue(cells.get(3))); // 最高价
            data.setLow(getValue(cells.get(4))); // 最低价
            data.setClose(getValue(cells.get(5))); // 收盘价
            data.setSettle(getValue(cells.get(7))); // 结算价
            data.setVolume(Long.parseLong(cells.get(10).innerText().trim().replaceAll(",", ""))); // 成交量
            data.setOpenInterest(Long.parseLong(cells.get(11).innerText().trim().replaceAll(",", ""))); // 持仓量
            data.setAmount(getValue(cells.get(13)).multiply(new BigDecimal(10000))); // 成交额
            items.add(data);
        }
        return items;
    }

    private BigDecimal getValue(final Locator el) {
        String value = el.innerText().trim();
        if ( value.equals("-") ) {
            log.warn("元素获取到的文本为-. el: {}", el);
            return BigDecimal.ZERO;
        }
        if ( StringUtils.hasText(value) ) {
            value = value.replaceAll(",", "");
            return new BigDecimal(value);
        }
        log.warn("元素获取到的文本为空. el: {}", el);
        return BigDecimal.ZERO;
    }

    /**
     * 提取品种代码
     * @param contractCode
     * @return
     */
    private static String extractProductCode(final String contractCode) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<contractCode.length(); i++) {
            // 遇到第一个数字，则跳出
            if ( contractCode.charAt(i) >= '0' && contractCode.charAt(i) <= '9' ) {
                break;
            }
            sb.append(contractCode.charAt(i));
        }
        return sb.toString();
    }

    /**
     * 下载excel文件
     * @param locator
     */
    private String downloadFile(final FrameLocator locator, final String currentTradeDate) {
        String filepath = DOWNLOAD_FILE_ROOT_DIR + File.separator + "DCE_" + currentTradeDate + ".xls";
        Locator el = locator.locator("div.tradeResult02 > ul > li:nth-child(2) > a");
        if ( null == el) {
            log.error("[DCE]获取地址: {} 上的元素[div.tradeResult02 > ul > li:nth-child(2) > a] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: div.tradeResult02 > ul > li:nth-child(2) > a");
        }
        log.info("[DCE]开始下载文件");
        try(Page page = locator.owner().page()) {
            // TODO: 无头浏览器无法下载，待解决
            Download download = page.waitForDownload(el::click); // 中间那个是下载excel的
            download.saveAs(Paths.get(filepath));
            log.info("[DCE]文件下载完成。 保存位置: {}", filepath);
        }
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
