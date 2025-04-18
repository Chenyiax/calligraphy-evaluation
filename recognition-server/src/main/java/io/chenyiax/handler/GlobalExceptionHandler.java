package io.chenyiax.handler;

import io.chenyiax.entity.RestBean;
import io.chenyiax.exception.SessionExpiredException;
import io.chenyiax.exception.TokenParseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(TokenParseException.class)
    public RestBean<String> handleTokenParseException(TokenParseException e) {
        return RestBean.failure(401, e.getMessage());
    }

    @ExceptionHandler(SessionExpiredException.class)
    public RestBean<String> handleSessionExpiredException(SessionExpiredException e) {
        return RestBean.failure(401, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public RestBean<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return RestBean.failure(400, e.getMessage());
    }

}
