package dev.branow;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.h2.tools.RunScript;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringJUnitConfig(TestDBConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class DBTest {

    private static final Path DATA_PATH = Path.of("src/test/resources/repositories/entity/test_data.sql");

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        System.out.println("SETUP DB DYNAMIC CONFIGURATION");
        registry.add(AvailableSettings.JAKARTA_JDBC_URL, postgres::getJdbcUrl);
        registry.add(AvailableSettings.JAKARTA_JDBC_USER, postgres::getUsername);
        registry.add(AvailableSettings.JAKARTA_JDBC_PASSWORD, postgres::getPassword);
        registry.add(AvailableSettings.JAKARTA_JDBC_DRIVER, postgres::getDriverClassName);
    }

    @BeforeAll
    static void setup() {
        if (!postgres.isRunning()) {
            System.out.println("STARTING TEST DB");
            postgres.start();
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("STOPPING TEST DB");
        postgres.stop();
        postgres.close();
    }

    @Autowired
    private Flyway flyway;
    @Autowired
    protected EntityManagerFactory factory;

    protected TransactionalEntityManager manager;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = factory.createEntityManager();
        manager = new TransactionalEntityManager(em);

        System.out.println("Cleaning up database");
        drop();
        System.out.println("Migrating");
        migrate();
        System.out.println("Filling up database");
        fill();
    }

    @AfterEach
    public void cleanUp() {
        em.clear();
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    protected void migrate() {
        flyway.migrate();
    }

    protected void fill() {
        try {
            execute(Files.newBufferedReader(DATA_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void drop() {
        execute("DROP SCHEMA public CASCADE");
        execute("CREATE SCHEMA public");
    }

    protected void execute(String sql) {
        execute(new StringReader(sql));
    }

    protected void execute(Reader sql) {
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            RunScript.execute(connection, sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
