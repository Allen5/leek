package club.cybecraftman.leek.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "成功"),
    UN_AUTHORIZED(401, "当前用户未登录或凭证已过期"),
    FORBIDDEN(403, "权限不足"),
    SYSTEM_ERROR(500, "系统异常"),

    BIZ_ERROR(1000, "业务异常"),
    PARAM_NOT_MATCH(1001, "请求数据格式或内容不必配"),
    ;

    ErrorCode(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    private final Integer code;

    private final String message;

}
