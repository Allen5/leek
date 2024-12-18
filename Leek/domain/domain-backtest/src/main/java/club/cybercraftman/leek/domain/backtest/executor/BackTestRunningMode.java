package club.cybercraftman.leek.domain.backtest.executor;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

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

    public static BackTestRunningMode parse(final String mode) {
        Optional<BackTestRunningMode> op = Arrays.stream(BackTestRunningMode.values()).filter(b -> b.getMode().equalsIgnoreCase(mode)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不支持的回测运行模式: " + mode);
        }
        return op.get();
    }

}
