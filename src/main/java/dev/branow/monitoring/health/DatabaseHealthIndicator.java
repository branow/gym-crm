package dev.branow.monitoring.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("databaseGymCrm")
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    static final String SELECT_TABLES_QUERY = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
    static final List<String> REQUIRED_TABLES = List.of("users", "trainees", "trainers", "trainings", "training_types");

    private final DataSource dataSource;

    @Override
    public Health health() {
        try {
            var presentTables = getDatabaseTables();
            var missingTables = REQUIRED_TABLES.stream()
                    .filter(name -> !presentTables.contains(name))
                    .toList();
            return Optional.of(missingTables)
                    .filter(List::isEmpty)
                    .map(_ -> Health.up())
                    .orElse(Health.down()
                            .withDetail("missingTables", missingTables))
                    .withDetail("presentTables", presentTables)
                    .build();
        } catch (SQLException e) {
            return Health.down(e).build();
        }
    }

    private List<String> getDatabaseTables() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_TABLES_QUERY)) {

            var tables = new ArrayList<String>();
            while (resultSet.next()) {
                tables.add(resultSet.getString("table_name"));
            }
            return tables;
        }
    }

}
