package club.cybercraftman.leek.common.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@ToString
public class CommonBar {

    private Date datetime;

    private String symbol;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal settle;

    private BigDecimal openInterest;

    private Integer volume;

    private BigDecimal amount;

    private BigDecimal multiplier;

    private BigDecimal priceTick;

}
