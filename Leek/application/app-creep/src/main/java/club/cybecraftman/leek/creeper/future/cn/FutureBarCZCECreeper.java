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
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.creeper.BaseCreeper;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FutureBarCZCECreeper extends BaseCreeper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doCreep(final Page page) throws LeekException {
        // 这里要先进入iframe
        FrameLocator locator = page.frameLocator("iframe[name=\"czcehqWin\"]");

        // step0: 获取当前交易日
        String currentTradeDate = getCurrentTradeDate(sdf);
        // step1: 校验交易日
        checkTradeDate(locator, currentTradeDate);
        // step2: 发送数据
        this.publishBars(BarType.DAILY, buildItems(locator, getCurrentTradeDate()));
    }

    private void checkTradeDate(final FrameLocator locator, final String currentTradeDate) throws LeekException {
        // step1: 获取页面上的交易日信息
        // meta: div.jysjtop > div.fr > span
        Locator el = locator.locator("div.jysjtop > div.fr > span");
        if ( el == null ) {
            log.error("[CZCE]获取地址: {} 上的元素[div.jysjtop > div.fr > span] 失败. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素定位失败: div.jysjtop > div.fr > span");
        }
        String content = el.innerText().trim();
        if ( !StringUtils.hasText(content) ) {
            log.error("[CZCE]地址: {}上的元素[div.jysjtop > div.fr > span]内容为空. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容为空: div.jysjtop > div.fr > span");
        }
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(content);
        if ( !matcher.find() ) {
            log.error("[CZCE]地址: {}上的元素[div.jysjtop > div.fr > span]内容不包含日期. event: {}", getEvent().getSource(), getEvent());
            throw new LeekRuntimeException("元素内容不包含日期: div.jysjtop > div.fr > span");
        }
        String candidate = matcher.group(0);
        if ( !currentTradeDate.equals(candidate) ) {
            log.error("[CZCE]当前的交易日为:{}，官方当前数据所属交易日为: {}。两者不一致，等待官方更新.", currentTradeDate, candidate);
            throw new LeekException("数据所属交易日和当前交易日不匹配");
        }
    }

    private List<FutureBarEventData> buildItems(final FrameLocator locator, final Date currentTradeDate) {
        // table#tab1 > tbody > tr
        List<Locator> lines = locator.locator("table#tab1 > tbody > tr").all();
        List<FutureBarEventData> items = new ArrayList<>();
        for(int i=0; i<lines.size()-1; i++) { // 去除最后一行
            List<Locator> cells = lines.get(i).locator("td").all();
            String contractCode = cells.get(0).innerText().trim();
            if ( contractCode.contains("小计") || contractCode.contains("总计") ) {
                log.warn("[CZCE]该行包含小计或总计，忽略. code: {}. line: {}", contractCode, lines.get(i));
                continue;
            }
            FutureBarEventData data = new FutureBarEventData();
            data.setDatetime(currentTradeDate);
            data.setProductCode(extractProductCode(contractCode));
            data.setContractCode(contractCode + "." + Exchange.CZCE.getCode().substring(0, 3)); // 合约代码
            data.setSymbol(data.getContractCode());
            data.setOpen(getValue(cells.get(2))); // 开盘价
            data.setHigh(getValue(cells.get(3))); // 最高价
            data.setLow(getValue(cells.get(4))); // 最低价
            data.setClose(getValue(cells.get(5))); // 收盘价
            data.setSettle(getValue(cells.get(6))); // 结算价
            data.setVolume(Long.parseLong(cells.get(9).innerText().trim().replaceAll(",", ""))); // 成交量
            data.setOpenInterest(Long.parseLong(cells.get(10).innerText().trim().replaceAll(",", ""))); // 持仓量
            data.setAmount(getValue(cells.get(12)).multiply(new BigDecimal(10000))); // 成交额
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

    private BigDecimal getValue(final Locator el) {
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
        // 中国期货市场郑商所官方行情数据
        return Market.CN.getCode().equals(event.getMarketCode()) &&
                FinanceType.FUTURE.getType().equals(event.getFinanceType()) &&
                DataType.BAR.getType().equals(event.getDataType()) &&
                SourceName.CZCE.getName().equals(event.getSourceName());
    }

}
