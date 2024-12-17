package club.cybercraftman.leek.common.event.etl;


import club.cybercraftman.leek.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class EtlEvent extends BaseEvent {

    private String functionId;

    private String masterUrl;

}
