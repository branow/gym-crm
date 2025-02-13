package dev.branow.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(SnakePhysicalNamingStrategy.class)
public class SnakePhysicalNamingStrategyTest {

    @Autowired
    private SnakePhysicalNamingStrategy strategy;

    @ParameterizedTest
    @MethodSource("provideTest")
    public void test(String input, String expected) {
        assertEquals(expected, toString(input, strategy::toPhysicalCatalogName));
        assertEquals(expected, toString(input, strategy::toPhysicalSchemaName));
        assertEquals(expected, toString(input, strategy::toPhysicalTableName));
        assertEquals(expected, toString(input, strategy::toPhysicalSequenceName));
        assertEquals(expected, toString(input, strategy::toPhysicalColumnName));
    }

    private String toString(String input, Strategy strategy) {
        return strategy.toPhysical(Identifier.toIdentifier(input), null).toString();
    }

    interface Strategy {
        Identifier toPhysical(Identifier identifier, JdbcEnvironment jdbcEnvironment);
    }

    private static Stream<Arguments> provideTest() {
        return Stream.of(
                Arguments.of("name", "name"),
                Arguments.of("camelCaseName", "camel_case_name"),
                Arguments.of("TitleCaseName", "title_case_name"),
                Arguments.of("snake_case_name", "snake_case_name")
        );
    }
}
