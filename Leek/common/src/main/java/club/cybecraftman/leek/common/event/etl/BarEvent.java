package club.cybecraftman.leek.common.event.etl;

import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class BarEvent<T>  extends BaseEvent {

    /**
     * @see BarType
     */
    private Integer barType;

    /**
     * @see club.cybecraftman.leek.common.constant.finance.FinanceType
     */
    private String financeType;

    /**
     * @see club.cybecraftman.leek.common.constant.finance.Market
     */
    private String marketCode;

    /**
     * 数据信息
     */
    private List<T> items;

}
