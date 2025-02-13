package dev.branow.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig({ FlywayConfig.class })
public class FlywayConfigTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("jakarta.persistence.jdbc.url", postgres::getJdbcUrl);
        registry.add("jakarta.persistence.jdbc.user", postgres::getUsername);
        registry.add("jakarta.persistence.jdbc.password", postgres::getPassword);
        registry.add("jakarta.persistence.jdbc.driver-class-name", postgres::getDriverClassName);
    }

    @BeforeAll
    public static void setup() {
        postgres.start();
    }

    @AfterAll
    public static void teardown() {
        postgres.stop();
        postgres.close();
    }

    @Autowired
    private Flyway flyway;

    @Test
    public void test() {
        assertNotNull(flyway);
    }

}
