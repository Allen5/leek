package club.cybercraftman.leek.common.constant.finance.future;

import lombok.Getter;

@Getter
public enum Exchange {

    SHFE("SHFE","上海期货交易所"),
    CZCE("CZCE","郑州商品交易所"),
    DCE("DCE","大连商品交易所"),
    CFFEX("CFFEX","中国金融期货交易所"),
    INE("INE","上海能源交易中心"),
    GFEX("GFEX","广州期货交易所"),

    ;

    Exchange(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    private final String code;

    private final String name;
}
