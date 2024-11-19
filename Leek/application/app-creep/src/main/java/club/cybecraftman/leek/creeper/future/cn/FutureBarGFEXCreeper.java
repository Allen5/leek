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
import com.microsoft.playwright.Download;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FutureBarGFEXCreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static final Map<String, String> productMaps;

    static {
        productMaps = new HashMap<>();
        productMaps.put("工业硅", "si");
        productMaps.put("碳酸锂", "lc");
    }


    @Override
    protected void doCreep(Page page) throws LeekException {
        // step0: 获取当前交易日
        String currentTradeDate = getCurrentTradeDate(sdf);
        // step1: 校验交易日
        checkTradeDate(page, currentTradeDate);
        // step2: 获取数据并推送
        this.publishBars(BarType.DAILY, buildItems(page, getCurrentTradeDate()));

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

    private List<FutureBarEventData> buildItems(final Page page, final Date currentTradeDate) {
        // div.layui-table-body > table > tbody > tr
        List<ElementHandle> lines = page.querySelectorAll("div.layui-table-body > table > tbody > tr");
        List<FutureBarEventData> items = new ArrayList<>();
        for(int i=0; i<lines.size(); i++) {
            List<ElementHandle> cells = lines.get(i).querySelectorAll("td");
            String name = cells.get(0).innerText().trim();
            if ( name.contains("小计") || name.contains("总计") ) {
                log.warn("[GFEX]商品名称包含小计或总计，忽略。 name: {}", name);
                continue;
            }
            FutureBarEventData data = new FutureBarEventData();
            data.setDatetime(currentTradeDate);
            if ( productMaps.containsKey(name) ) {
                data.setProductCode(productMaps.get(name));
            } else {
                log.error("获取到的名称不在定义范围内。 name: {}. products_map: {}", name, productMaps);
                continue;
            }
            data.setContractCode(data.getProductCode() + cells.get(1).innerText().trim());
            data.setSymbol(data.getContractCode());
            data.setOpen(getValue(cells.get(2)));
            data.setHigh(getValue(cells.get(3)));
            data.setLow(getValue(cells.get(4)));
            data.setClose(getValue(cells.get(5)));
            data.setSettle(getValue(cells.get(7)));
            data.setVolume(Long.parseLong(cells.get(10).innerText().trim().replaceAll(",", "")));
            data.setOpenInterest(Long.parseLong(cells.get(11).innerText().trim().replaceAll(",", "")));
            data.setAmount(getValue(cells.get(13)).multiply(new BigDecimal(10000)));
            items.add(data);
        }
        return items;
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
        // 中国期货市场广期所官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.GFEX.getName().equals(event.getSourceName());

    }
}
