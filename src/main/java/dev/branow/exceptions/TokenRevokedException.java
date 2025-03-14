package dev.branow.exceptions;

import org.springframework.security.core.AuthenticationException;

public class TokenRevokedException extends AuthenticationException {

    private static final String MESSAGE = "Token has been revoked";

    public TokenRevokedException() {
        this(null);
    }

    public TokenRevokedException(Throwable cause) {
        super(MESSAGE, cause);
    }

}
