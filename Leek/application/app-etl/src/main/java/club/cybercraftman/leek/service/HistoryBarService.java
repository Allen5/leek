package club.cybercraftman.leek.service;

import club.cybercraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.domain.financedata.future.FutureDailyBarService;
import club.cybercraftman.leek.dto.BigQuantBarItem;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 历史行情数据处理
 */
@Service
@Slf4j
public class HistoryBarService {

    /**
     * 每次处理1000条
     */
    private static final Integer BATCH_COUNT = 10000;

    /**
     * 日行情处理
     */
    @Autowired
    private FutureDailyBarService barService;


    /**
     * 导入指定年份的数据
     * @param filepath
     * @param year
     */
    @Transactional
    public void importBigQuantHistoryBars(final String filepath, final Integer year) {
        log.info("开始导入文件: {}. 并删除{}年份的数据", filepath, year);
        barService.truncateBarsByYear(year);
        this.importBigQuantHistoryBars(filepath);
    }



    /**
     * BigQuant的历史行情数据导入处理
     * @param dir
     */
    @Transactional
    public void importBigQuantHistoryBars(final String dir, final String appendix) throws LeekException {
        if ( !Files.exists(Paths.get(dir)) ) {
            log.error("目录不存在: {}。 请检查", dir);
            throw new LeekException("目录不存在: " + dir);
        }
        File[] files = new File(dir).listFiles();
        if ( null == files || files.length == 0 ) {
            log.error("获取目录: {}下的文件列表失败", dir);
            throw new LeekException("获取目录: " + dir + " 下的文件列表失败");
        }
        List<File> subFiles = Arrays.stream(files)
                .filter(File::isFile) // 筛选类型为文件
                .filter(f -> f.getName().toUpperCase().endsWith(appendix.toUpperCase())) // 后缀名匹配
                .collect(Collectors.toList());
        if ( CollectionUtils.isEmpty(subFiles) ) {
            log.error("目录: {} 下不存在 {} 数据文件", dir, appendix.toUpperCase());
            throw new LeekException("目录: " + dir + "下不存在 " +appendix.toUpperCase()+ " 数据文件");
        }
        barService.truncateBars();
        subFiles.stream().parallel().forEach(f -> this.importBigQuantHistoryBars(f.getAbsolutePath()));
    }

    /**
     * BigQuant的历史行情数据导入处理
     * @param filepath
     */
    public void importBigQuantHistoryBars(final String filepath) {
        log.info("开始导入文件: {}", filepath);
        EasyExcel.read(filepath, BigQuantBarItem.class, new PageReadListener<BigQuantBarItem>(items -> {
            // 对于contractCode需要特殊处理。
            List<FutureBarEventData> datas = items.stream().
                    filter(item ->
                            !item.getContractCode().contains("0000") &&
                            !item.getContractCode().contains("9999") &&
                            !item.getContractCode().contains(item.getProductCode() + "8")) // 过滤包含0000、品种代码8|88|888|888、9999的数据
                    .map(item -> {
                        FutureBarEventData data = new FutureBarEventData();
                        data.setDatetime(item.getDatetime());
                        data.setProductCode(item.getProductCode());
                        data.setContractCode(convertContractCode(item.getDatetime(), item.getContractCode(), item.getProductCode()));
                        data.setSymbol(data.getContractCode());
                        data.setOpen(item.getOpen());
                        data.setHigh(item.getHigh());
                        data.setLow(item.getLow());
                        data.setClose(item.getClose());
                        data.setSettle(item.getSettle());
                        data.setVolume(item.getVolume());
                        data.setOpenInterest(item.getOpenInterest());
                        data.setAmount(item.getAmount());
                        return data;
                    }).collect(Collectors.toList());
            log.info("开始插入日行情数据, 共: {} 条", datas.size());
            barService.batchInsert(datas);
        }, BATCH_COUNT)).sheet(0).doRead();
    }

    /**
     * 处理合约代码
     * @param datetime
     * @param original
     * @return
     */
    private String convertContractCode(final Date datetime, final String original, final String productCode) {
        // 判断是否仅包含3位数字
        Pattern pattern = Pattern.compile(productCode + "[0-9]{3}\\.");
        Matcher matcher = pattern.matcher(original);
        if ( !matcher.find() ) {
            return original;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(productCode); // 品种代码
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(datetime);
        builder.append(date, 2, 3);
        builder.append(matcher.group(0).replace(productCode, "").replace(".", ""));// 原始代码数字
        builder.append(original.substring(original.indexOf("."))); // 追加交易所信息
        return builder.toString();
    }

}
