package club.cybecraftman.leek.reader.future;

import club.cybecraftman.leek.common.constant.finance.future.Exchange;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import com.alibaba.excel.EasyExcel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 郑商所每日行情文件读取
 */
public class CZCEExcelReader {

    /**
     * 需要过滤的行
     */
    private static final Set<String> INVALID_CONTRACT_CODE;

    /**
     * 指定第二行为标题行
     */
    private static final Integer HEAD_ROW_NUM = 2;

    static {
        INVALID_CONTRACT_CODE = new HashSet<>();
        INVALID_CONTRACT_CODE.add("小计");
        INVALID_CONTRACT_CODE.add("总计");
    }

    /**
     * 读取郑商所每日行情文件
     * @param filepath
     * @return
     */
    public static List<FutureBarEventData> readDailyBar(final Date datetime, final String filepath) {
        List<CZCEBarItem> items = EasyExcel.read(filepath).headRowNumber(HEAD_ROW_NUM).sheet(0).doReadSync();
        return items.stream().parallel()
                .filter(item -> !INVALID_CONTRACT_CODE.contains(item.getContractCode()))
                .map(item -> {
                    FutureBarEventData data = new FutureBarEventData();
                    data.setDatetime(datetime);
                    data.setContractCode(convertContractCode(item.getContractCode()));
                    data.setProductCode(extractProductCode(item.getContractCode()));
                    data.setSymbol(data.getContractCode());
                    data.setOpen(item.getOpen());
                    data.setHigh(item.getHigh());
                    data.setLow(item.getLow());
                    data.setVolume(item.getVolume());
                    data.setOpenInterest(item.getOpenInterest());
                    data.setAmount(item.getAmount());
                    data.setClose(item.getClose());
                    return data;
                }).collect(Collectors.toList());
    }

    private static String convertContractCode(final String original) {
        // 取年份的倒数第二位作为前缀. 如: 2024，取2。
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        StringBuilder sb = new StringBuilder();
        sb.append(year.charAt(year.length() - 1));
        for (int i=0; i<original.length(); i++) {
            if ( sb.charAt(i) >= '0' && sb.charAt(i) <= '9' ) {
                sb.append(sb.charAt(i));
            }
        }
        sb.append(".").append(Exchange.CZCE.getCode().toUpperCase());
        return sb.toString();
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
            if ( sb.charAt(i) >= '0' && sb.charAt(i) <= '9' ) {
                break;
            }
            sb.append(sb.charAt(i));
        }
        return sb.toString();
    }

}
