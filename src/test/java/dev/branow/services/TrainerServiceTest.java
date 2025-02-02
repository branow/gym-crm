package dev.branow.services;

import dev.branow.model.Trainer;
import dev.branow.model.User;
import dev.branow.repositories.Repository;
import dev.branow.repositories.TrainerRepository;
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

@SpringJUnitConfig(classes = TrainerService.class)
public class TrainerServiceTest {

    @MockitoBean
    private TrainerRepository repository;
    @MockitoBean
    private TrainingRepository trainingRepository;
    @MockitoBean
    private List<Repository<Long, ? extends User>> repositories;
    @MockitoBean
    private PasswordGenerator passwordGenerator;
    @MockitoBean
    private UsernameGenerator usernameGenerator;

    @MockitoSpyBean
    private TrainerService service;

    @Test
    public void testUpdate() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Smith");
        trainer.setUserId(123L);
        when(repository.findById(trainer.getUserId())).thenReturn(Optional.of(trainer));
        when(repository.update(trainer)).thenReturn(trainer);
        var actual = service.update(trainer);
        assertEquals(trainer, actual);
        verify(service, times(1)).update(trainer.getUserId(), trainer);
    }

    @Test
    public void testDeleteById() {
        var userId = 123L;
        service.deleteById(userId);
        verify(trainingRepository, times(1))
                .deleteAllByTrainerId(userId);
        verify(repository, times(1)).deleteById(userId);
    }

}
