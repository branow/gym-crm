package dev.branow.cli;

import dev.branow.dtos.TraineeDto;
import dev.branow.services.TraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(TraineeCommands.class)
public class TraineeCommandsTest {

    @MockitoBean
    private TraineeService service;

    @Test
    public void testGet(@Qualifier("getTrainee") @Autowired Command command) {
        when(service.getByUsername(any())).thenReturn(new TraineeDto());
        command.execute("get-tee", "username");
        verify(service, times(1)).getByUsername(any());
    }

    @Test
    public void testCreate(@Qualifier("createTrainee") @Autowired Command command) {
        when(service.create(any())).thenReturn(new TraineeDto());
        command.execute("crt-tee", ".", ".");
        verify(service, times(1)).create(any());
    }

    @Test
    public void testUpdate(@Qualifier("updateTrainee") @Autowired Command command) {
        when(service.update(any())).thenReturn(new TraineeDto());
        command.execute("upt-tee", "123", ".", ".");
        verify(service, times(1)).update(any());
    }

    @Test
    public void testDelete(@Qualifier("deleteTrainee") @Autowired Command command) {
        command.execute("del-tee", "username");
        verify(service, times(1)).deleteByUsername(any());
    }

    @Test
    public void addFavoriteTrainer(@Qualifier("addFavoriteTrainer") @Autowired Command command) {
        when(service.addFavoriteTrainer(any(), any())).thenReturn(new TraineeDto());
        command.execute("upt-tee", "username1", "username2", ".");
        verify(service, times(1)).addFavoriteTrainer(any(), any());
    }

    @Test
    public void deleteFavoriteTrainer(@Qualifier("deleteFavoriteTrainer") @Autowired Command command) {
        when(service.deleteFavoriteTrainer(any(), any())).thenReturn(new TraineeDto());
        command.execute("upt-tee", "username1", "username2", ".");
        verify(service, times(1)).deleteFavoriteTrainer(any(), any());
    }

}
