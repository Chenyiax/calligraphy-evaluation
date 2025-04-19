package io.chenyiax.exception;

import lombok.Getter;

@Getter
public class WeChatApiException extends BusinessException {
    public WeChatApiException(String message) {
        super(1001, message);
    }
}