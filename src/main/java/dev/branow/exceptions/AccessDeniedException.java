package dev.branow.exceptions;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super("Access denied: " + message);
    }

}
