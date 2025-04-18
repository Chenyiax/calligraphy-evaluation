package io.chenyiax.exception;

public class TokenParseException extends RuntimeException {
    public TokenParseException() {
        super("Token parsing failure");
    }

    public TokenParseException(String message) {
        super(message);
    }
}
