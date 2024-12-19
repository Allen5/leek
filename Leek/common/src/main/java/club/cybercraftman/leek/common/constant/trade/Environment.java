package club.cybercraftman.leek.common.constant.trade;

import lombok.Getter;

@Getter
public enum Environment {
    BACK_TEST(0, "回测"),
    MOCK(1, "模拟盘"),
    REAL(2, "实盘")
    ;

    Environment(final Integer env, final String description) {
        this.env = env;
        this.description = description;
    }

    private final Integer env;

    private final String description;
}
