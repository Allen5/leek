package club.cybecraftman.leek.reader.future;

import club.cybecraftman.leek.common.constant.finance.future.Exchange;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.core.utils.excel.reader.listener.SimpleReadListener;
import com.alibaba.excel.EasyExcel;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 郑商所每日行情文件读取
 */
public class CFFEXExcelReader {

    /**
     * 需要过滤的行
     */
    private static final Set<String> INVALID_CONTRACT_CODE;

    /**
     * 指定第二行为标题行
     */
    private static final Integer HEAD_ROW_NUM = 0;

    static {
        INVALID_CONTRACT_CODE = new HashSet<>();
        INVALID_CONTRACT_CODE.add("小计");
        INVALID_CONTRACT_CODE.add("合计");
    }

    /**
     * 读取郑商所每日行情文件
     * @param filepath
     * @return
     */
    public static List<FutureBarEventData> readDailyBar(final Date datetime, final String filepath) {
        List<CFFEXBarItem> items = EasyExcel.read(filepath, CFFEXBarItem.class, new SimpleReadListener())
                .headRowNumber(HEAD_ROW_NUM)
                .sheet(0)
                .doReadSync();
        return items.stream().parallel()
                .filter(item -> !INVALID_CONTRACT_CODE.contains(item.getContractCode()))
                .filter(item -> !item.getContractCode().contains("-P-") && !item.getContractCode().contains("-C-"))
                .map(item -> {
                    FutureBarEventData data = new FutureBarEventData();
                    data.setDatetime(datetime);
                    data.setProductCode(extractProductCode(item.getContractCode()));
                    data.setContractCode(item.getContractCode() + "." + Exchange.CFFEX.getCode().substring(0, 3));
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
     * @param contractCode 合约代码
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

}
