package club.cybercraftman.leek.common.constant.trade;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum CommissionCategory {

    TRADE_FEE(100, "交易手续费", "交易手续费"),

    STAMP_TAX(900, "印花税", "A股股票市场收取的印花税"),

    ;

    CommissionCategory(final Integer category, final String name, final String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    /**
     * 类别
     */
    private final Integer category;

    private final String name;

    private final String description;

    public static CommissionCategory parse(final Integer category) {
        Optional<CommissionCategory> op = Arrays.stream(CommissionCategory.values()).filter(c -> category.equals(c.getCategory())).findAny();
        if ( op.isPresent() ) {
            return op.get();
        }
        throw new LeekRuntimeException("不支持的手续费、服务费类别: " + category);
    }

}
