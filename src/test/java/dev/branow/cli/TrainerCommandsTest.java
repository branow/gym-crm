package dev.branow.cli;

import dev.branow.dtos.TrainerDto;
import dev.branow.model.Trainer;
import dev.branow.services.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(TrainerCommands.class)
public class TrainerCommandsTest {

    @MockitoBean
    private TrainerService service;

    @Test
    public void testGetAllNotAssigned(@Qualifier("getTrainerNotAssigned") @Autowired Command command) {
        when(service.getAllNotAssignedOnTraineeByTraineeUsername(any())).thenReturn(List.of());
        command.execute("get-ter-na", "username");
        verify(service, times(1)).getAllNotAssignedOnTraineeByTraineeUsername(any());
    }

    @Test
    public void testGet(@Qualifier("getTrainer") @Autowired Command command) {
        when(service.getByUsername(any())).thenReturn(new TrainerDto());
        command.execute("get-ter", "username");
        verify(service, times(1)).getByUsername(any());
    }

    @Test
    public void testCreate(@Qualifier("createTrainer") @Autowired Command command) {
        when(service.create(any())).thenReturn(new TrainerDto());
        command.execute("crt-ter", ".", ".", ".");
        verify(service, times(1)).create(any());
    }

    @Test
    public void testUpdate(@Qualifier("updateTrainer") @Autowired Command command) {
        when(service.update(any())).thenReturn(new TrainerDto());
        command.execute("upt-ter", "123", ".", ".", ".");
        verify(service, times(1)).update(any());
    }

}
