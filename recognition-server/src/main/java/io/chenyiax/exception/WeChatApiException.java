package io.chenyiax.exception;

public class WeChatApiException extends RuntimeException {
    private final Integer errorCode;

    public WeChatApiException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}