package io.chenyiax.exception;

public class JsonException extends BusinessException {
    public JsonException(String message) {
        super(1004, message);
    }
}
