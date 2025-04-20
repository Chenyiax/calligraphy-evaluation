package io.chenyiax.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义业务异常类，继承自 RuntimeException，用于在业务逻辑处理过程中抛出异常。
 * 该异常类包含错误码和错误消息，方便在业务出错时传递详细的错误信息。
 */
@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    /**
     * 业务异常的错误码，用于标识不同类型的业务错误。
     */
    private int code;

    /**
     * 业务异常的错误消息，用于描述业务错误的具体信息。
     */
    private String message;
}