package dev.branow.monitoring.health;

import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(InactiveUsersIndicator.class)
public class InactiveUsersIndicatorTest {

    @MockitoBean
    private UserRepository repository;

    @Autowired
    private InactiveUsersIndicator indicator;

    @Test
    public void testHealth_up() {
        when(repository.findAll()).thenReturn(List.of(
                User.builder().isActive(true).build(),
                User.builder().isActive(true).build()
        ));
        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    public void testHealth_down() {
        when(repository.findAll()).thenReturn(List.of(
                User.builder().isActive(true).build(),
                User.builder().isActive(false).build()
        ));
        assertEquals(Status.DOWN, indicator.health().getStatus());
    }

}
