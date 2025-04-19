package io.chenyiax.exception;

public class UserCreationException extends BusinessException {
    public UserCreationException(String message) {
        super(1003, message);
    }
}
