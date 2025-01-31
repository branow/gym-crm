package dev.branow.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SimplePasswordGeneratorTest {

    private final SimplePasswordGenerator generator = new SimplePasswordGenerator();

    @Test
    public void testGenerate() {
        var iterations = 1000;
        var passwords = new String[iterations];
        for (int i = 0; i < iterations; i++) {
            var password = generator.generate();
            // Check if password contains illegal characters
            for (int j = 0; j < password.length(); j++) {
                assertNotEquals(-1, SimplePasswordGenerator.CHARACTERS.indexOf(password.charAt(j)));
            }
            // Check if password is unique
            for (int k = 0; k < i; k++) {
                assertNotEquals(password, passwords[k]);
            }
            passwords[i] = password;
        }
    }
}
