package dev.branow.exceptions;

public class BadCredentialsException extends RuntimeException {

    private static final String message = "Invalid username or password";

    public BadCredentialsException() {
        super(message);
    }

    public BadCredentialsException(Exception cause) {
        super(message, cause);
    }

}
