package club.cybecraftman.leek.common.constant.finance;

import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Market {
    CN("CN", "中国市场"),
    HK("HK", "香港市场"),
    US("US", "美国市场"),
    ;

    Market(final String code, final String description) {
        this.code = code;
        this.description = description;
    }


    /**
     * 市场代码
     */
    private final String code;

    /**
     * 描述
     */
    private final String description;

    public static Market parse(final String code) throws LeekException {
        if ( !StringUtils.hasText(code) ) {
            throw new LeekRuntimeException("交易市场代码不能为空");
        }
        Optional<Market> op = Arrays.stream(Market.values()).filter(m -> m.getCode().equalsIgnoreCase(code)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekException("不支持的交易市场代码: " + code);
        }
        return op.get();
    }

}
