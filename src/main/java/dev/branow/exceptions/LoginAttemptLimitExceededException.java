package dev.branow.exceptions;

import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

public class LoginAttemptLimitExceededException extends AuthenticationException {

    private static final String MESSAGE = "You have reached the login attempt limit. Please try again at %s.";

    private static String getMessage(TemporalAmount timeout) {
        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        var datetime = LocalDateTime.now().plus(timeout).format(formatter);
        return String.format(MESSAGE, datetime);
    }

    public LoginAttemptLimitExceededException(TemporalAmount timeout) {
        this(timeout, null);
    }

    public LoginAttemptLimitExceededException(TemporalAmount timeout, Throwable cause) {
        super(getMessage(timeout), cause);
    }

}
