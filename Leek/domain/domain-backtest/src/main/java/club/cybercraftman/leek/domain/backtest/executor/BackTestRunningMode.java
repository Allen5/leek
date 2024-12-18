package club.cybercraftman.leek.domain.backtest.executor;

import lombok.Getter;

@Getter
public enum BackTestRunningMode {

    DEV("develop", "开发模式回测"),
    TEST("test", "测试模式回测"),
    VERIFY("verify", "验证模式回测"),

    ;

    BackTestRunningMode(final String mode, final String description) {
        this.mode = mode;
        this.description = description;
    }

    private final String mode;

    private final String description;

}
