package dev.branow.cli;

import dev.branow.dtos.TrainingDto;
import dev.branow.model.Training;
import dev.branow.services.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(TrainingCommands.class)
public class TrainingCommandsTest {

    @MockitoBean
    private TrainingService service;

    @Test
    public void testGetAllByTrainee(@Qualifier("getTrainingByTrainee") @Autowired Command command) {
        when(service.getAllByTraineeUsernameCriteria(any())).thenReturn(List.of());
        command.execute("get-tng-tee", "username");
        verify(service, times(1)).getAllByTraineeUsernameCriteria(any());
    }

    @Test
    public void testGetAllByTrainer(@Qualifier("getTrainingByTrainer") @Autowired Command command) {
        when(service.getAllByTrainerUsernameCriteria(any())).thenReturn(List.of());
        command.execute("get-tng-ter", "username");
        verify(service, times(1)).getAllByTrainerUsernameCriteria(any());
    }

    @Test
    public void testCreate(@Qualifier("createTraining") @Autowired Command command) {
        when(service.create(any())).thenReturn(new TrainingDto());
        command.execute("crt-tng", "1", "2", ".", ".", ".", ".");
        verify(service, times(1)).create(any());
    }

}
