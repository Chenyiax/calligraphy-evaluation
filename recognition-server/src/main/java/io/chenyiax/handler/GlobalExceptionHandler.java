package io.chenyiax.handler;

import io.chenyiax.entity.RestBean;
import io.chenyiax.exception.BusinessException;
import io.chenyiax.exception.HunYuanException;
import io.chenyiax.exception.JwtException;
import io.chenyiax.exception.WeChatApiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器类，用于统一处理应用程序中抛出的各种异常。
 * 使用 @RestControllerAdvice 注解，表明这是一个全局的控制器增强类，
 * 可以捕获控制器层抛出的异常并返回统一格式的响应数据。
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
