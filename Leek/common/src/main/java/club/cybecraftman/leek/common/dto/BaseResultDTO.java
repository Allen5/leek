package club.cybecraftman.leek.common.dto;

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
        resp.setCode(200);
        resp.setMessage("success");
        resp.setSuccess(true);
        resp.setData(data);
        return resp;
    }

}
