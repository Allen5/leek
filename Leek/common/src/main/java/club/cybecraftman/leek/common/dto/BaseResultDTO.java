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

}
