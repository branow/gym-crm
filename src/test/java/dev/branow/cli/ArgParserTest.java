package dev.branow.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArgParserTest {

    @ParameterizedTest
    @MethodSource("provideTestOf")
    public void testOf(String[] args, List<String> values, Map<String, String> options) {
        var parser = ArgsParser.of(args);
        assertEquals(values, parser.getValues());
        assertEquals(options, Map.copyOf(parser.getOptions()));
    }

    @Test
    public void testArgumentGet() {
        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser.Argument<>(Optional.empty(), String.class).get());
        assertEquals("value", new ArgsParser.Argument<>(Optional.of("value"), String.class).get());
    }

    @Test
    public void testArgumentOrDefault() {
        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser.Argument<>(Optional.empty(), String.class).orDefault("default"));
        assertEquals("default", new ArgsParser.Argument<>(Optional.of(ArgsParser.DEFAULT), String.class).orDefault("default"));
        assertEquals("value", new ArgsParser.Argument<>(Optional.of("value"), String.class).orDefault("default"));
    }

    @Test
    public void testArgumentOrElse() {
        assertEquals("other", new ArgsParser.Argument<>(Optional.empty(), String.class).orElse("default", "other"));
        assertEquals("default", new ArgsParser.Argument<>(Optional.of(ArgsParser.DEFAULT), String.class).orElse("default", "other"));
        assertEquals("value", new ArgsParser.Argument<>(Optional.of("value"), String.class).orElse("default", "other"));
    }

    private static Stream<Arguments> provideTestOf() {
        return Stream.of(
                Arguments.of(
                        "key -n noption value1 --some other value".replaceAll("-", ArgsParser.OPTION).split(" "),
                        List.of("key", "value1", "value"),
                        Map.of("n", "noption", "-some", "other")
                ),
                Arguments.of(
                        "-a opa -b -c opc value".replaceAll("-", ArgsParser.OPTION).split(" "),
                        List.of("value"),
                        Map.of("a", "opa", "b", "", "c", "opc")
                )
        );
    }

}
