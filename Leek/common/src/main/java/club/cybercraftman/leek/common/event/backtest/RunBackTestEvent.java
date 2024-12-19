package club.cybercraftman.leek.common.event.backtest;

import club.cybercraftman.leek.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class RunBackTestEvent extends BaseEvent {

    private String marketCode;

    private String financeType;

    private String strategyClassName;

    private Map<String, Object> strategyParams;

    private String runningMode;

    private Date startDate;

    private Date endDate;

    private Integer minBars;

    private String capital;

    // TODO: 补充回测业务参数

}
