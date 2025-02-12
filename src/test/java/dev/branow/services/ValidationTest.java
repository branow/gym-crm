package dev.branow.services;

import jakarta.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTest {

    public static<T> void testValidation(boolean isValid, Runnable operation) {
        if (isValid) {
            assertDoesNotThrow(operation::run);
        } else {
            assertThrows(ValidationException.class, operation::run);
        }
    }

}
