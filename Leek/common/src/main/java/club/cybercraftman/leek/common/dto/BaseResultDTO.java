package club.cybercraftman.leek.common.dto;

import club.cybercraftman.leek.common.exception.ErrorCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseResultDTO<T> {

    private Integer code;

    private String message;

    private boolean isSuccess;

    private T data;

    public static <T> BaseResultDTO<T> success(final T data) {
        BaseResultDTO<T> resp = new BaseResultDTO<>();
        resp.setCode(ErrorCode.SUCCESS.getCode());
        resp.setMessage(ErrorCode.SUCCESS.getMessage());
        resp.setSuccess(true);
        resp.setData(data);
        return resp;
    }

    public static <T> BaseResultDTO<T> fail(final ErrorCode err) {
        BaseResultDTO<T> resp = new BaseResultDTO<>();
        resp.setCode(err.getCode());
        resp.setMessage(err.getMessage());
        resp.setSuccess(false);
        resp.setData(null);
        return resp;
    }

    public static <T> BaseResultDTO<T> fail(final Integer code, final String errMessage) {
        BaseResultDTO<T> resp = new BaseResultDTO<>();
        resp.setCode(code);
        resp.setMessage(errMessage);
        resp.setSuccess(false);
        resp.setData(null);
        return resp;
    }

}
