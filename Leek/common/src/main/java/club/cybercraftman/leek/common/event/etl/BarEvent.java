package club.cybercraftman.leek.common.event.etl;

import club.cybercraftman.leek.common.constant.finance.BarType;
import club.cybercraftman.leek.common.event.BaseEvent;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class BarEvent  extends BaseEvent {

    /**
     * @see BarType
     */
    private Integer barType;

    /**
     * @see FinanceType
     */
    private String financeType;

    /**
     * @see Market
     */
    private String marketCode;

    /**
     * 数据信息
     */
    private JSONArray items;

}
