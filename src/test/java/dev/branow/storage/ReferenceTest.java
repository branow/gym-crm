package dev.branow.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReferenceTest {

    @ParameterizedTest
    @MethodSource("provideTestGetType")
    public void testGetType(Reference<?> reference, String expectedType) {
        var actualType = reference.getType().getTypeName();
        assertEquals(expectedType, actualType);
    }

    @Test
    public void testConstructor_noSpecifiedType_throwException() {
        assertThrows(IllegalArgumentException.class, () -> new Reference() {});
    }

    private static Stream<Arguments> provideTestGetType() {
        return Stream.of(
                Arguments.of(
                        new Reference<Boolean>() {},
                        "java.lang.Boolean"
                ),
                Arguments.of(
                        new Reference<List<String>>() {},
                        "java.util.List<java.lang.String>"
                ),
                Arguments.of(
                        new Reference<HashMap<String, Integer>>() {},
                        "java.util.HashMap<java.lang.String, java.lang.Integer>"
                )
        );
    }

}
