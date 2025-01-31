package dev.branow.storage;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class JsonStorageTest {

    @ParameterizedTest
    @MethodSource("provideTest")
    public<T> void testSaveGet_normalFlow(T object, Reference<T> reference) {
        var stringStorage = new StringStorage();
        var storage = new JsonStorage(stringStorage);
        storage.save("data", object);
        var actual = storage.get("data", reference);
        assertEquals(object, actual.orElse(null));
    }

    private static Stream<Arguments> provideTest() {
        return Stream.of(
                Arguments.of(
                        List.of("a", "b", "c", "d"),
                        new Reference<List<String>>() {}
                ),
                Arguments.of(
                        "some string",
                        new Reference<String>() {}
                ),
                Arguments.of(
                        Map.of("a", 1, "b", 2, "c", 3),
                        new Reference<Map<String, Integer>>() {}
                ),
                Arguments.of(
                        new Custom("hello"),
                        new Reference<Custom>() {}
                ),
                Arguments.of(
                        Map.of("a", new Custom("a"),"b", new Custom("b"), "c", new Custom("c")),
                        new Reference<Map<String, Custom>>() {}
                )
        );
    }

    private static class StringStorage implements Storage {
        private String value = "";
        public InputStream read() {
            return new ByteArrayInputStream(value.getBytes());
        }
        public void write(InputStream stream) {
            try {
                value = new String(stream.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Custom {
        public String name;

        public Custom() {}

        public Custom(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Custom custom = (Custom) o;
            return Objects.equals(name, custom.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public String toString() {
            return "Custom{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

}
