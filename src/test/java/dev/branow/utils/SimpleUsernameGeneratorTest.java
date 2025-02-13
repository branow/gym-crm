package dev.branow.utils;

import dev.branow.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleUsernameGeneratorTest {

    private final SimpleUsernameGenerator generator = new SimpleUsernameGenerator();

    @ParameterizedTest
    @MethodSource("provideTestGenerate")
    public void testGenerate(User user, Stream<User> users, String expected) {
        var actual = generator.generate(user, users);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> provideTestGenerate() {
        return Stream.of(
                Arguments.of(
                        User.builder().id(1L).firstName("fn").lastName("ln").build(),
                        Stream.of(),
                        "fn.ln"
                ),
                Arguments.of(
                        User.builder().id(4L).firstName("fn").lastName("ln").build(),
                        Stream.of(User.builder().id(5L).firstName("fn").lastName("ln").username("fn.ln").build()),
                        "fn.ln1"
                ),
                Arguments.of(
                        User.builder().id(1L).firstName("fn").lastName("ln").build(),
                        Stream.of(
                                User.builder().id(1L).firstName("fn").lastName("ln").username("fn.ln").build(),
                                User.builder().id(3L).firstName("fn").lastName("ln").username("fn.ln1").build(),
                                User.builder().id(4L).firstName("fn").lastName("ln").username("fn.ln2").build()
                        ),
                        "fn.ln3"
                ),
                Arguments.of(
                        User.builder().id(1L).firstName("fn").lastName("ln").build(),
                        Stream.of(
                                User.builder().id(1L).firstName("fn").lastName("ln").username("fn.ln1").build(),
                                User.builder().id(2L).firstName("fn").lastName("ln").username("fn.ln10").build()
                                ),
                        "fn.ln11"
                )
        );
    }

}
