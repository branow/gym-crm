package dev.branow.monitoring.health;

import dev.branow.model.TrainingType;
import dev.branow.repositories.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TrainingTypeAvailabilityIndicator.class)
public class TrainingTypeAvailabilityIndicatorTest {

    @MockitoBean
    private TrainingTypeRepository repository;

    @Autowired
    private TrainingTypeAvailabilityIndicator indicator;

    @Test
    public void testHealth_up() {
        when(repository.findAll()).thenReturn(List.of(
                new TrainingType(1L, "")
        ));
        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    public void testHealth_down() {
        when(repository.findAll()).thenReturn(List.of());
        assertEquals(Status.DOWN, indicator.health().getStatus());
    }

}
