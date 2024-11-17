package club.cybecraftman.leek.creeper.future.cn;

import club.cybecraftman.leek.common.constant.creep.DataType;
import club.cybecraftman.leek.common.constant.creep.SourceName;
import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.constant.finance.future.Exchange;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.creeper.BaseCreeper;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class FutureBarINECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final Set<String> INVALID_MONTH = new HashSet<>();

    private static final Set<String> IGNORE_PRODUCTS = new HashSet<>();

    private static final Map<String, String> productMaps = new HashMap<>();


    static {

        INVALID_MONTH.add("小计");
        INVALID_MONTH.add("总计");

        IGNORE_PRODUCTS.add("原油TAS"); // 这个是要忽略的

        productMaps.put("铜(BC)", "bc");
        productMaps.put("原油", "sc");
        productMaps.put("低硫燃料油", "lu");
        productMaps.put("20号胶", "nr");
        productMaps.put("SCFIS欧线", "ec");
    }


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
        // div.data > table > tbody > table > tbody 拿到所有的tables
        this.publishBars(BarType.DAILY, buildItems(page, getCurrentTradeDate()));
    }

    @Override
    public boolean isSupport(CreepEvent event) {
        // 中国期货市场上期能源官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.INE.getName().equals(event.getSourceName());
    }

    private List<FutureBarEventData> buildItems(final Page page, final Date currentTradeDate) throws LeekException {
        // div.data > table > tbody > table > tbody 拿到所有的tables
        List<ElementHandle> tables = page.querySelectorAll("div.data > table > tbody > table > tbody");
        if ( CollectionUtils.isEmpty(tables) ) {
            log.error("[INE]地址: {}页面元素[div.data > table > tbody > table > tbody]定位失败", getEvent().getSource());
            throw new LeekException("[INE]页面元素[div.data > table > tbody > table > tbody]定位失败");
        }
        List<FutureBarEventData> items = new ArrayList<>();
        for (ElementHandle table: tables) {
            ElementHandle nameEl = table.querySelector("tr.data_tab_tr_pinz");
            if ( null == nameEl ) {
                log.error("[INE]页面元素[tr.data_tab_tr_pinz]定位失败");
                throw new LeekException("[INE]页面元素[tr.data_tab_tr_pinz]定位失败");
            }
            String name = nameEl.innerText().trim();
            name = name.replace("商品名称:", "");
            if ( IGNORE_PRODUCTS.contains(name) ) {
                log.warn("[INE]品种: {}不处理[原油TAS] ", name);
                continue;
            }
            String productCode =  extractProductCode(name);
            List<ElementHandle> lines = table.querySelectorAll("tr");
            for(int i=2; i<lines.size(); i++) { // 忽略前两行
                List<ElementHandle> cells = lines.get(i).querySelectorAll("td");
                if ( CollectionUtils.isEmpty(cells) ) {
                    log.error("[INE]品种: {} 下不存在数据", name);
                    continue;
                }
                String month = cells.get(0).innerText().trim();
                if ( INVALID_MONTH.contains(month) ) {
                    log.warn("[INE]品种: {} 下的月份: {} 忽略", name, month);
                    continue;
                }
                FutureBarEventData data = new FutureBarEventData();
                data.setDatetime(currentTradeDate);
                data.setProductCode(productCode);
                data.setContractCode(productCode + month + "." + Exchange.INE.getCode());
                data.setSymbol(data.getContractCode());
                data.setOpen(new BigDecimal(cells.get(2).innerText().trim()));
                data.setHigh(new BigDecimal(cells.get(3).innerText().trim()));
                data.setLow(new BigDecimal(cells.get(4).innerText().trim()));
                data.setClose(new BigDecimal(cells.get(5).innerText().trim()));
                data.setSettle(new BigDecimal(cells.get(6).innerText().trim()));
                data.setVolume(Long.parseLong(cells.get(9).innerText().trim()));
                data.setAmount(new BigDecimal(cells.get(10).innerText().trim()).multiply(new BigDecimal(10000)));
                data.setOpenInterest(Long.parseLong(cells.get(11).innerText().trim()));
                items.add(data);
            }
        }
        return items;
    }

    private String extractProductCode(final String productName) throws LeekException {
        if ( productMaps.containsKey(productName) ) {
            return productMaps.get(productName);
        }
        log.error("[INE]品种名称: {} 不正确。 未找到对应代码!", productName);
        throw new LeekException("品种名称不正确: " + productName);
    }


}
