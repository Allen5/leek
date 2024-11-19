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
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
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
        this.publishBars(BarType.DAILY, buildItems(page, getCurrentTradeDate()));
    }

    private List<FutureBarEventData> buildItems(final Page page, final Date currentTradeDate) {
        // tbody.tbody_tj > tr
        page.waitForSelector("tbody.tbody_tj > tr");
        List<ElementHandle> lines = page.querySelectorAll("tbody.tbody_tj > tr");
        List<FutureBarEventData> items = new ArrayList<>();
        for(int i=0; i<lines.size(); i++) {
            List<ElementHandle> cells = lines.get(i).querySelectorAll("td");
            String contractCode = cells.get(0).innerText().trim();
            if ( contractCode.contains("小计") || contractCode.contains("总计") ) {
                log.warn("[CFFEX]合约代码包含小计或总计，忽略. code: {}", contractCode);
                continue;
            }
            FutureBarEventData data = new FutureBarEventData();
            data.setDatetime(currentTradeDate);
            data.setProductCode(extractProductCode(contractCode));
            data.setContractCode(contractCode);
            data.setSymbol(data.getContractCode());
            data.setOpen(getValue(cells.get(1)));
            data.setHigh(getValue(cells.get(2)));
            data.setLow(getValue(cells.get(3)));
            data.setVolume(Long.parseLong(cells.get(4).innerText().trim().replaceAll(",", "")));
            data.setAmount(getValue(cells.get(5)).multiply(new BigDecimal(10000)));
            data.setOpenInterest(Long.parseLong(cells.get(6).innerText().trim().replaceAll(",", "")));
            data.setClose(getValue(cells.get(8)));
            data.setSettle(getValue(cells.get(9)));
            items.add(data);
        }
        return items;
    }

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

    private BigDecimal getValue(final ElementHandle el) {
        String value = el.innerText().trim();
        if ( StringUtils.hasText(value) ) {
            value = value.replaceAll(",", "");
            return new BigDecimal(value);
        }
        log.warn("元素获取到的文本为空. el: {}", el);
        return BigDecimal.ZERO;
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
