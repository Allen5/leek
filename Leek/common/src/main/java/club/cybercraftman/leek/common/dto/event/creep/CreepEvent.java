package club.cybercraftman.leek.common.dto.event.creep;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreepEvent {

    private String marketCode;

    private String financeType;

    private String dataType;

    private String sourceName;

    private String source;

}
