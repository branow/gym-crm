package dev.branow.services;

import dev.branow.model.Trainee;
import dev.branow.model.User;
import dev.branow.repositories.Repository;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainingRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = TraineeService.class)
public class TraineeServiceTest {

    @MockitoBean
    private TraineeRepository repository;
    @MockitoBean
    private TrainingRepository trainingRepository;
    @MockitoBean
    private List<Repository<Long, ? extends User>> repositories;
    @MockitoBean
    private PasswordGenerator passwordGenerator;
    @MockitoBean
    private UsernameGenerator usernameGenerator;

    @MockitoSpyBean
    private TraineeService service;

    @Test
    public void testUpdate() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setUserId(123L);
        when(repository.findById(trainee.getUserId())).thenReturn(Optional.of(trainee));
        when(repository.update(trainee)).thenReturn(trainee);
        var actual = service.update(trainee);
        assertEquals(trainee, actual);
        verify(service, times(1)).update(trainee.getUserId(), trainee);
    }

    @Test
    public void testDeleteById() {
        var userId = 123L;
        service.deleteById(userId);
        verify(trainingRepository, times(1))
                .deleteAllByTraineeId(userId);
        verify(repository, times(1)).deleteById(userId);
    }

}