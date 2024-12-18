package club.cybercraftman.leek.common.event.backtest;

import club.cybercraftman.leek.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class RunBackTestEvent extends BaseEvent {

    private String marketCode;

    private String financeType;

    private String strategyId;

    private String runningMode;

    private Date startDate;

    private Date endDate;

    private Integer minBars;

    private String capital;

    // TODO: 补充回测业务参数

}
