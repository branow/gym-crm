package dev.branow.services;

import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = TrainingService.class)
public class TrainingServiceTest {

    @MockitoBean
    private TrainingRepository repository;
    @MockitoBean
    private TrainerRepository trainerRepository;
    @MockitoBean
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainingService service;

    @Test
    public void testGetById_isPresent_returnTraining() {
        var expected = new Training();
        var id = 123L;
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        var actual = service.getById(id);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetById_isAbsent_throwException() {
        var id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(id));
    }

    @Test
    public void testGetAll() {
        var expected = List.of(
                Training.builder().build(),
                Training.builder().trainingName("cardio").build()
        );
        when(repository.findAll()).thenReturn(expected.stream());
        var actual = service.getAll();
        assertEquals(expected, actual);
    }

    @Test
    public void testCreate_existingTrainerAndTrainee_create() {
        var expected = Training.builder()
                .trainingName("cardio")
                .trainerId(123L)
                .trainingId(321L)
                .build();
        when(traineeRepository.findById(expected.getTraineeId())).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findById(expected.getTrainerId())).thenReturn(Optional.of(new Trainer()));
        when(repository.create(expected)).thenReturn(expected);
        var actual = service.create(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testCreate_notExistingTrainer_throwException() {
        var expected = Training.builder()
                .trainingName("cardio")
                .trainerId(123L)
                .trainingId(321L)
                .build();
        when(traineeRepository.findById(expected.getTraineeId())).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findById(expected.getTrainerId())).thenReturn(Optional.empty());
        when(repository.create(expected)).thenReturn(expected);
        assertThrows(EntityNotFoundException.class, () -> service.create(expected));
    }

    @Test
    public void testCreate_notExistingTrainee_throwException() {
        var expected = Training.builder()
                .trainingName("cardio")
                .trainerId(123L)
                .trainingId(321L)
                .build();
        when(traineeRepository.findById(expected.getTraineeId())).thenReturn(Optional.empty());
        when(trainerRepository.findById(expected.getTrainerId())).thenReturn(Optional.of(new Trainer()));
        when(repository.create(expected)).thenReturn(expected);
        assertThrows(EntityNotFoundException.class, () -> service.create(expected));
    }
}
