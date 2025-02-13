package dev.branow.log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.stream.Stream;

import static dev.branow.log.Level.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(Slf4jLogBuilderFactory.class)
public class Slf4jLogBuilderFactoryTest {

    @Autowired
    private Slf4jLogBuilderFactory factory;


    @Test
    public void testNewLogBuilder() {
        var builder = factory.newLogBuilder(INFO, "TestSource", "TestOperation");
        assertInstanceOf(Slf4jLogBuilder.class, builder);
    }

    @ParameterizedTest
    @MethodSource("provideTestToSlf4jLevel")
    public void testToSlf4jLevel(Level level, org.slf4j.event.Level expected) {
        var actual = Slf4jLogBuilderFactory.toSlf4jLevel(level);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> provideTestToSlf4jLevel() {
        return Stream.of(
                Arguments.of(null, org.slf4j.event.Level.INFO),
                Arguments.of(ERROR, org.slf4j.event.Level.ERROR),
                Arguments.of(WARN, org.slf4j.event.Level.WARN),
                Arguments.of(INFO, org.slf4j.event.Level.INFO),
                Arguments.of(DEBUG, org.slf4j.event.Level.DEBUG),
                Arguments.of(TRACE, org.slf4j.event.Level.TRACE)
        );
    }

}
