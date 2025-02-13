package dev.branow.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
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

    @Test
    public void testParse_() {
        var args = "-p --d opt1 value my-value -o opt".split(" ");
        var expected = "my-value";
        var actual = ArgsParser.of(args).parse(1, String.class).get();
        assertEquals(expected, actual);
    }

    @Test
    public void testParse_flag() {
        var args = "-p --dash opt1 value my-value -o opt".split(" ");
        var expected = "opt1";
        var actual = ArgsParser.of(args).parse("-dash", String.class).get();
        assertEquals(expected, actual);
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

    @Test
    public<T> void testArgumentParseValue_invalidType_throwException() {
        class A {}
        var parser = new ArgsParser.Argument<>(Optional.of("string"), A.class);
        assertThrows(IllegalArgumentException.class, parser::get);
    }

    @ParameterizedTest
    @MethodSource("provideTestArgumentParseValue")
    public<T> void testArgumentParseValue_validType_parseObject(String value, T expected) {
        var parser = new ArgsParser.Argument<>(Optional.of(value), expected.getClass());
        assertEquals(expected, parser.get());
    }

    private static Stream<Arguments> provideTestArgumentParseValue() {
        return Stream.of(
                Arguments.of("string", "string"),
                Arguments.of("123", 123),
                Arguments.of("123", 123L),
                Arguments.of("2023-12-01", LocalDate.of(2023, 12, 1))
        );
    }

















}
