package club.cybercraftman.leek.common.bean;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class DateRange {

    public DateRange(final Date start, final Date end) {
        this.start = start;
        this.end = end;
    }

    private final Date start;

    private final Date end;

}