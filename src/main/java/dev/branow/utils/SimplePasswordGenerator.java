package dev.branow.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Stream;

@Component
public class SimplePasswordGenerator implements PasswordGenerator {

    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
    public static final int DEFAULT_LENGTH = 10;
    private static final Random RANDOM = new SecureRandom();

    public String generate(int length) {
        return Stream
                .generate(() -> RANDOM.nextInt(CHARACTERS.length()))
                .map(CHARACTERS::charAt)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String generate() {
        return generate(DEFAULT_LENGTH);
    }

}
