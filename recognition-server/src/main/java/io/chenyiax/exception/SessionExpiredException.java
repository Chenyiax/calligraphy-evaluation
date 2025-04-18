package io.chenyiax.exception;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException() {
        super("Session has expired, please log in again");
    }

    public SessionExpiredException(String message) {
        super(message);
    }
}