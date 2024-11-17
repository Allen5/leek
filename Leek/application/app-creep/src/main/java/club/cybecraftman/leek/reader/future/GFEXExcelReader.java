package club.cybecraftman.leek.reader.future;

import club.cybecraftman.leek.common.constant.finance.future.Exchange;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.core.utils.excel.reader.listener.SimpleReadListener;
import com.alibaba.excel.EasyExcel;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 郑商所每日行情文件读取
 */
public class GFEXExcelReader {

    /**
     * 需要过滤的行
     */
    private static final Set<String> INVALID_CONTRACT_CODE;

    private static final Map<String, String> productMaps;

    /**
     * 指定第二行为标题行
     */
    private static final Integer HEAD_ROW_NUM = 0;

    static {
        INVALID_CONTRACT_CODE = new HashSet<>();
        INVALID_CONTRACT_CODE.add("小计");
        INVALID_CONTRACT_CODE.add("总计");

        productMaps = new HashMap<>();
        productMaps.put("工业硅", "si");
        productMaps.put("碳酸锂", "lc");
    }

    /**
     * 读取郑商所每日行情文件
     * @param filepath
     * @return
     */
    public static List<FutureBarEventData> readDailyBar(final Date datetime, final String filepath) {
        List<GFEXBarItem> items = EasyExcel.read(filepath, GFEXBarItem.class, new SimpleReadListener())
                .headRowNumber(HEAD_ROW_NUM)
                .sheet(0)
                .doReadSync();
        return items.stream().parallel()
                .filter(item -> !INVALID_CONTRACT_CODE.contains(item.getProductName()))
                .map(item -> {
                    FutureBarEventData data = new FutureBarEventData();
                    data.setDatetime(datetime);
                    data.setProductCode(extractProductCode(item.getProductName()));
                    data.setContractCode(convertContractCode(data.getProductCode(), item.getDeliveryMonth()));
                    data.setSymbol(data.getContractCode());
                    data.setOpen(item.getOpen());
                    data.setHigh(item.getHigh());
                    data.setLow(item.getLow());
                    data.setSettle(item.getSettle());
                    data.setVolume(item.getVolume());
                    data.setOpenInterest(item.getOpenInterest());
                    data.setAmount(item.getAmount().multiply(BigDecimal.valueOf(10000)));
                    data.setClose(item.getClose());
                    return data;
                }).collect(Collectors.toList());
    }

    /**
     * 提取品种代码
     * @param productName 品种名称
     * @return
     */
    private static String extractProductCode(final String productName) {
        if ( productMaps.containsKey(productName) ) {
            return productMaps.get(productName);
        }
        throw new LeekRuntimeException("不支持的品种名称: " + productName);
    }

    private static String convertContractCode(final String productCode, final String deliveryMonth) {
        return String.format("%s%s.%s", productCode, deliveryMonth, Exchange.GFEX.getCode().substring(0, 3));
    }

}
