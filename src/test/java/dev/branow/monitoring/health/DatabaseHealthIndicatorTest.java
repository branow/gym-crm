package dev.branow.monitoring.health;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(DatabaseHealthIndicator.class)
public class DatabaseHealthIndicatorTest {

    @MockitoBean
    private DataSource dataSource;
    @MockitoBean
    private Connection connection;
    @MockitoBean
    private Statement statement;
    @MockitoBean
    private ResultSet resultSet;

    @Autowired
    private DatabaseHealthIndicator indicator;

    @Test
    @SneakyThrows
    public void testHeath_connectionException_returnDown() {
        when(dataSource.getConnection()).thenThrow(SQLException.class);
        assertEquals(Status.DOWN, indicator.health().getStatus());
    }

    @Test
    @SneakyThrows
    public void testHeath_missingTables_returnDown() {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(DatabaseHealthIndicator.SELECT_TABLES_QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertEquals(Status.DOWN, indicator.health().getStatus());
    }

    @Test
    @SneakyThrows
    public void testHeath_returnUp() {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(DatabaseHealthIndicator.SELECT_TABLES_QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, true, true, true, false);
        when(resultSet.getString("table_name")).thenReturn("users", "trainees", "trainers", "trainings", "training_types");
        assertEquals(Status.UP, indicator.health().getStatus());
    }

}
