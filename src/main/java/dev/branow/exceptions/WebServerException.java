package dev.branow.exceptions;

public class WebServerException extends RuntimeException {

    public WebServerException(String message, Exception cause) {
        super(message, cause);
    }

}
