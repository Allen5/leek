package club.cybercraftman.leek.creeper.future.cn;

import club.cybercraftman.leek.common.constant.creep.DataType;
import club.cybercraftman.leek.common.constant.creep.SourceName;
import club.cybercraftman.leek.common.constant.finance.BarType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.constant.finance.future.Exchange;
import club.cybercraftman.leek.common.event.creep.CreepEvent;
import club.cybercraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.creeper.BaseCreeper;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class FutureBarINECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final Set<String> INVALID_MONTH = new HashSet<>();

    private static final Map<String, String> productMaps = new HashMap<>();


    static {

        INVALID_MONTH.add("小计");
        INVALID_MONTH.add("总计");

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
        page.waitForSelector("div > table.el-table__body > tbody");
        List<ElementHandle> tables = page.querySelectorAll("div > table.el-table__body > tbody");
        if ( CollectionUtils.isEmpty(tables) ) {
            log.error("[INE]地址: {}页面元素[div.data > table > tbody > table > tbody]定位失败", getEvent().getSource());
            throw new LeekException("[INE]页面元素[div.data > table > tbody > table > tbody]定位失败");
        }
        List<FutureBarEventData> items = new ArrayList<>();
        int index = 1;
        for (ElementHandle table: tables) {
            if ( index == tables.size() ) {
                continue;
            }
            index++;
            ElementHandle nameEl = table.querySelector("tr.special_row_type");
            if ( null == nameEl ) {
                log.error("[INE]页面元素[tr.special_row_type]定位失败");
                throw new LeekException("[INE]页面元素[tr.special_row_type]定位失败");
            }
            String name = nameEl.innerText().trim();
            name = name.replace("商品名称:", "");
            if ( !productMaps.containsKey(name) ) {
                log.warn("[INE]品种: {}不处理 ", name);
                continue;
            }
            String productCode =  extractProductCode(name);
            List<ElementHandle> lines = table.querySelectorAll("tr");
            for(int i=1; i<lines.size(); i++) { // 忽略前1行
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
                data.setOpen(getValue(cells.get(2)));
                data.setHigh(getValue(cells.get(3)));
                data.setLow(getValue(cells.get(4)));
                data.setClose(getValue(cells.get(5)));
                data.setSettle(getValue(cells.get(6)));
                if ( data.getProductCode().equals("sc") ) { // 原油有TAS数据，要后移动一行
                    data.setVolume(Long.parseLong(cells.get(10).innerText().trim()));
                    data.setAmount(new BigDecimal(cells.get(11).innerText().trim()).multiply(new BigDecimal(10000)));
                    data.setOpenInterest(Long.parseLong(cells.get(12).innerText().trim()));
                } else {
                    data.setVolume(Long.parseLong(cells.get(9).innerText().trim()));
                    data.setAmount(new BigDecimal(cells.get(10).innerText().trim()).multiply(new BigDecimal(10000)));
                    data.setOpenInterest(Long.parseLong(cells.get(11).innerText().trim()));
                }
                items.add(data);
            }
        }
        return items;
    }

    private BigDecimal getValue(final ElementHandle el) {
        String value = el.innerText().trim();
        if ( !StringUtils.hasText(value) ) {
            log.warn("元素获取到的文本为空. el: {}", el);
            return BigDecimal.ZERO;
        }
        if ( value.equals("--") ) {
            log.warn("元素获取到的文本为[--]. el: {}", el);
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }


    private String extractProductCode(final String productName) throws LeekException {
        if ( productMaps.containsKey(productName) ) {
            return productMaps.get(productName);
        }
        log.error("[INE]品种名称: {} 不正确。 未找到对应代码!", productName);
        throw new LeekException("品种名称不正确: " + productName);
    }


}
