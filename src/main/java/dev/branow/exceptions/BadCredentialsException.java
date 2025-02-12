package dev.branow.exceptions;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {
        super("Invalid username or password");
    }

}
