package club.cybecraftman.leek.common.constant.finance;

import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum FinanceType {

    FUTURE("future", "期货市场"),
    STOCK("stock", "股票市场"),

    ;

    FinanceType(final String type, final String description) {
        this.type = type;
        this.description = description;
    }

    @Getter
    private final String type;

    @Getter
    private final String description;

    public static FinanceType parse(final String type) throws LeekException {
        if ( !StringUtils.hasText(type) ) {
            throw new LeekRuntimeException("金融产品类型不能为空");
        }
        Optional<FinanceType> op = Arrays.stream(FinanceType.values()).filter(m -> m.getType().equalsIgnoreCase(type)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekException("不支持的金融产品类型: " + type);
        }
        return op.get();
    }
}
