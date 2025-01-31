package dev.branow.repositories.map;

import dev.branow.repositories.Repository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = IdGenerator.IncrementIdGenerator.class)
public class IdGeneratorTest {

    @Autowired
    private IdGenerator.IncrementIdGenerator incrementIdGenerator;

    @ParameterizedTest
    @MethodSource("provideTestIncrementIdGenerator")
    public void testIncrementIdGenerator(Stream<Long> ids, Long expected) {
        var actual = incrementIdGenerator.generate(new Repository<>() {
            @Override
            public Object create(Object trainee) {
                return null;
            }

            @Override
            public Stream<Object> findAll() {
                return Stream.of();
            }

            @Override
            public Stream<Long> findIdAll() {
                return ids;
            }

            @Override
            public Optional<Object> findById(Long traineeId) {
                return Optional.empty();
            }

            @Override
            public Object update(Object trainee) {
                return null;
            }

            @Override
            public void deleteById(Long traineeId) {}
        });
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> provideTestIncrementIdGenerator() {
        return Stream.of(
                Arguments.of(Stream.of(), 1L),
                Arguments.of(Stream.of(1L), 2L),
                Arguments.of(Stream.of(4L, 2L, 13L, 5L), 14L)
        );
    }

}
