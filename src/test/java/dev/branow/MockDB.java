package dev.branow;

import dev.branow.config.SnakePhysicalNamingStrategy;
import dev.branow.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.h2.tools.RunScript;
import org.hibernate.cfg.AvailableSettings;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class MockDB {

    private static final String
            URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
            USER = "sa",
            PASSWORD = "";

    public static MockDB getInstance() {
        var path = Path.of("src/test/resources/repositories/entity/test_data.sql");
        return getInstance(path);
    }

    public static MockDB getInstance(Path initFile) {
        var randomUrl = URL.replace("testdb", "testdb" + new Random().nextInt());
        return new MockDB(randomUrl, USER, PASSWORD, initFile);
    }

    private final String url;
    private final String user;
    private final String password;
    private final Path initFile;

    private EntityManagerFactory factory;
    private EntityManager manager;

    public MockDB(String url, String user, String password) {
        this(url, user, password, null);
    }

    public MockDB(String url, String user, String password, Path initFile) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.initFile = initFile;
    }

    public void initialize() {
        var migDir = Path.of("src/main/resources/db/migration");

        checkAndDropDatabase();

        try {
            Files.list(migDir).forEach(this::executeSqlFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (initFile != null) {
            executeSqlFile(initFile);
        }
    }


    private void checkAndDropDatabase() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            stmt.execute("DROP ALL OBJECTS DELETE FILES");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeSqlFile(Path path) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = Files.readString(path);
            sql = sql.replaceAll("generated always as identity", "auto_increment");
            RunScript.execute(connection, new StringReader(sql));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EntityManager getManager() {
        if (factory == null || !factory.isOpen()) {
            throw new IllegalStateException("Factory is not open");
        }
        if (manager == null) {
            manager = factory.createEntityManager();
        }
        return manager;
    }

    public EntityManagerFactory connect(Class<?>... annotatedClasses) {
        var configuration = new org.hibernate.cfg.Configuration()
                .setProperty(AvailableSettings.JAKARTA_JDBC_URL, url)
                .setProperty(AvailableSettings.JAKARTA_JDBC_USER, user)
                .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, password)
                .setPhysicalNamingStrategy(new SnakePhysicalNamingStrategy())
                .addAnnotatedClass(Trainee.class)
                .addAnnotatedClass(Trainer.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Training.class)
                .addAnnotatedClass(TrainingType.class);
        for (var annotatedClass : annotatedClasses) {
            configuration.addAnnotatedClass(annotatedClass);
        }
        factory = configuration.buildSessionFactory();
        return factory;
    }

    public void closeTransaction() {
        if (manager.getTransaction().isActive()) {
            manager.getTransaction().rollback();
        }
        manager.clear();
    }

    public void close() {
        manager.close();
        factory.close();
    }

}
