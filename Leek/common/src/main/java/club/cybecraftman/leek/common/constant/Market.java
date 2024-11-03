package club.cybecraftman.leek.common.constant;

import lombok.Getter;

public enum Market {
    CN("CN", "中国市场"),
    USA("USA", "美国市场"),
    ;

    Market(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    @Getter
    private String code;

    @Getter
    private String description;

}
