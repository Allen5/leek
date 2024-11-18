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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class FutureBarSHFECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final Set<String> INVALID_MONTH = new HashSet<>();

    private static final Map<String, String> productMaps = new HashMap<>();

    private static final Set<String> IGNORE_PRODUCTS = new HashSet<>();

    static {

        INVALID_MONTH.add("小计");

        // 这些是上期能源的品种
        IGNORE_PRODUCTS.add("铜(BC)");
        IGNORE_PRODUCTS.add("原油");
        IGNORE_PRODUCTS.add("低硫燃料油");
        IGNORE_PRODUCTS.add("20号胶");
        IGNORE_PRODUCTS.add("SCFIS欧线");
        IGNORE_PRODUCTS.add("原油TAS"); // 这个是要忽略的


        productMaps.put("铜", "cu");
        productMaps.put("铝", "al");
        productMaps.put("锌", "zn");
        productMaps.put("铅", "pb");
        productMaps.put("镍", "ni");
        productMaps.put("锡", "sn");
        productMaps.put("氧化铝", "ao");
        productMaps.put("黄金", "au");
        productMaps.put("白银", "ag");
        productMaps.put("螺纹钢", "rb");
        productMaps.put("线材", "wr");
        productMaps.put("热轧卷板", "hc");
        productMaps.put("不锈钢", "ss");
        productMaps.put("燃料油", "fu");
        productMaps.put("石油沥青", "bu");
        productMaps.put("丁二烯橡胶", "br");
        productMaps.put("天然橡胶", "ru");
        productMaps.put("纸浆", "sp");

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
        // 构建并推送消息
        publishBars(BarType.DAILY, buildItems(page, getCurrentTradeDate()));
    }

    @Override
    public boolean isSupport(CreepEvent event) {
        // 中国期货市场上期所官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.SHFE.getName().equals(event.getSourceName());
    }

    private List<FutureBarEventData> buildItems(final Page page, final Date currentTradeDate) throws LeekException {
        // step1:  div.el-table__body-wrapper > table > tbody
        List<ElementHandle> tables = page.querySelectorAll("div.el-table__body-wrapper > table > tbody");
        if ( CollectionUtils.isEmpty(tables) ) {
            log.error("[SHFE]地址: {} 页面元素[div.el-table__body-wrapper > table > tbody]定位失败", getEvent().getSource());
            throw new LeekException("[SHFE]页面元素定位失败");
        }
        List<FutureBarEventData> items = new ArrayList<>();
        for (ElementHandle table : tables) {
            // 获取第一行元素，其为商品名
            ElementHandle head = table.querySelector("tr:nth-child(1)");
            String name = head.querySelector("td:nth-child(1) > div").innerText().trim();
            if ( name.contains("商品名称") ) {
                name = name.replace("商品名称:", "");
            } else {
                // 多余的table
                continue;
            }
            log.info("[SHFE]开始处理品种: {}", name);
            if ( IGNORE_PRODUCTS.contains(name) ) {
                log.warn("[SHFE]品种: {} 不属于SHFE。归属于 INE 或不处理[原油TAS] ", name);
                continue;
            }
            // 转换为品种名称
            String productCode = extractProductCode(name);
            List<ElementHandle> lines = table.querySelectorAll("tr");
            if ( CollectionUtils.isEmpty(lines) ) {
                log.error("[SHFE]品种: {} 下不存在数据", name);
                continue;
            }
            for (int i=1; i<lines.size(); i++) {
                List<ElementHandle> cells = lines.get(i).querySelectorAll("td");
                if ( CollectionUtils.isEmpty(cells) ) {
                    log.error("[SHFE]品种: {} 所处的行: {} 不存在数据", name, i);
                    continue;
                }
                String month = cells.get(0).innerText().trim();
                if ( INVALID_MONTH.contains(month) ) {
                    log.warn("[SHFE]品种: {} 下的月份: {} 忽略", name, month);
                    continue;
                }
                FutureBarEventData data = new FutureBarEventData();
                data.setDatetime(currentTradeDate);
                data.setProductCode(productCode);
                data.setContractCode(productCode + month + "." + Exchange.SHFE.getCode().substring(0, 3));
                data.setSymbol(data.getContractCode());
                data.setOpen(getValue(cells.get(2)));
                data.setHigh(getValue(cells.get(3)));
                data.setLow(getValue(cells.get(4)));
                data.setClose(getValue(cells.get(5)));
                data.setSettle(getValue(cells.get(6)));
                data.setVolume(Long.parseLong(cells.get(9).innerText().trim()));
                data.setAmount(new BigDecimal(cells.get(10).innerText().trim()).multiply(new BigDecimal(10000)));
                data.setOpenInterest(Long.parseLong(cells.get(11).innerText().trim()));
                items.add(data);
            }
        }
        return items;
    }

    private BigDecimal getValue(final ElementHandle el) {
        String value = el.innerText().trim();
        if ( StringUtils.hasText(value) ) {
            return new BigDecimal(value);
        }
        log.warn("元素获取到的文本为空. el: {}", el);
        return BigDecimal.ZERO;
    }

    private String extractProductCode(final String productName) throws LeekException {
        if ( productMaps.containsKey(productName) ) {
            return productMaps.get(productName);
        }
        log.error("[SHFE]品种名称: {} 不正确。 未找到对应代码!", productName);
        throw new LeekException("品种名称不正确: " + productName);
    }

}
