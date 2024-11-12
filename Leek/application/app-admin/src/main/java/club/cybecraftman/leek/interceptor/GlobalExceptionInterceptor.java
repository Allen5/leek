package club.cybecraftman.leek.interceptor;

import club.cybecraftman.leek.common.dto.BaseResultDTO;
import club.cybecraftman.leek.common.exception.AuthorizedException;
import club.cybecraftman.leek.common.exception.ErrorCode;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionInterceptor {

    /**
     * 用户身份验证异常
     * @param resp
     * @param e
     * @return
     */
    @ExceptionHandler(AuthorizedException.class)
    public BaseResultDTO<Object> handleAuthorizedException(HttpServletResponse resp, AuthorizedException e) {
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return BaseResultDTO.fail(ErrorCode.UN_AUTHORIZED);
    }

    @ExceptionHandler(LeekException.class)
    public BaseResultDTO<Object> handleLeekException(LeekException e) {
        log.error("业务异常: ", e);
        return BaseResultDTO.fail(ErrorCode.BIZ_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(LeekRuntimeException.class)
    public BaseResultDTO<Object> handleLeekRuntimeException(LeekRuntimeException e) {
        log.error("运行时异常: ", e);
        return BaseResultDTO.fail(ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResultDTO<Object> handleException(Exception e) {
        log.error("系统异常: ", e);
        return BaseResultDTO.fail(ErrorCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResultDTO<Object> handleRuntimeException(Exception e) {
        log.error("系统异常: ", e);
        return BaseResultDTO.fail(ErrorCode.SYSTEM_ERROR);
    }

}
