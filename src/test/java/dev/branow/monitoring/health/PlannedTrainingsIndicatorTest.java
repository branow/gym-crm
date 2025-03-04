package dev.branow.monitoring.health;

import dev.branow.model.Training;
import dev.branow.model.TrainingType;
import dev.branow.repositories.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(PlannedTrainingsIndicator.class)
public class PlannedTrainingsIndicatorTest {

    @MockitoBean
    private TrainingRepository repository;

    @Autowired
    private PlannedTrainingsIndicator indicator;

    @Test
    public void testHealth_up() {
        when(repository.findAll()).thenReturn(List.of(
                Training.builder().date(LocalDate.now().plusDays(3)).build()
        ));
        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    public void testHealth_down() {
        when(repository.findAll()).thenReturn(List.of(
                Training.builder().date(LocalDate.now().minusDays(3)).build()
        ));
        assertEquals(Status.DOWN, indicator.health().getStatus());
    }
}
