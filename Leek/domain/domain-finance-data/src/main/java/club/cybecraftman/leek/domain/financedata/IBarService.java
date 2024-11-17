package club.cybecraftman.leek.domain.financedata;

import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import com.alibaba.fastjson2.JSONArray;

import java.util.List;

/**
 * 行情处理
 */
public interface IBarService {

    /**
     * 根据isSupport获取指定的行情数据处理类
     * @param market
     * @param financeType
     * @param barType
     * @return
     */
    boolean isSupport(final Market market, final FinanceType financeType, final BarType barType);


    /**
     * 处理增量Bar数据
     * @param bars
     */
    void handleBars(final JSONArray bars);

    /**
     * 清空所有数据
     */
    void truncateBars();

    /**
     * 删除该年份的数据
     * @param year
     */
    void truncateBarsByYear(final String year);

    /**
     * 批量插入
     * @param items
     */
    void batchInsert(final List<FutureBarEventData> items);

}
