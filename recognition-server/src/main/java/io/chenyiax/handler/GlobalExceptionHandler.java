package io.chenyiax.handler;

import io.chenyiax.entity.RestBean;
import io.chenyiax.exception.BusinessException;
import io.chenyiax.exception.HunYuanException;
import io.chenyiax.exception.JwtException;
import io.chenyiax.exception.WeChatApiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class serves as a global exception handler, intercepting and handling various exceptions
 * thrown during the application's execution. It returns a unified RESTful response format
 * using the {@link RestBean} class.
 *
 * @see RestControllerAdvice
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JwtException.class)
    public RestBean<String> handleJwtException(JwtException e) {
        return RestBean.failure(401, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public RestBean<String> handleBusinessException(BusinessException e) {
        return RestBean.failure(406, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public RestBean<String> handleException(Exception e) {
        return RestBean.failure(400, e.getMessage());
    }
}
