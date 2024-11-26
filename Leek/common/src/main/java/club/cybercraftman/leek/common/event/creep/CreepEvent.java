package club.cybercraftman.leek.common.event.creep;

import club.cybercraftman.leek.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class CreepEvent extends BaseEvent {

    private String marketCode;

    private String financeType;

    private String dataType;

    private String sourceName;

    private String source;

}
