package dev.branow.services;

import dev.branow.dtos.CreateTrainerDto;
import dev.branow.dtos.UpdateTrainerDto;
import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.mappers.TrainerMapper;
import dev.branow.model.Trainer;
import dev.branow.model.TrainingType;
import dev.branow.repositories.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TrainerRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private TrainingTypeService trainingTypeService;

    private final TrainerMapper mapper = new TrainerMapper();
    private TrainerService service;

    @BeforeEach
    void setUp() {
        service = new TrainerService(repository, mapper, userService, trainingTypeService);
    }

    @Test
    public void testGetAllNotAssignedOnTraineeByTraineeUsername() {
        var username = "username";
        var expected = List.of(new Trainer(), new Trainer());
        when(repository.findAllNotAssignedOnTraineeByTraineeUsername(username)).thenReturn(expected);
        var actual = service.getAllNotAssignedOnTraineeByTraineeUsername(username);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetById_withPresentTrainer_returnTrainer() {
        var id = 123L;
        var trainer = new Trainer();
        trainer.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(trainer));
        var actual = service.getById(id);
        assertEquals(trainer, actual);
    }

    @Test
    public void testGetById_withAbsentTrainer_throwException() {
        var id = 123L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(id));
    }

    @Test
    public void testGetByUsername_withPresentTrainer_returnTrainer() {
        var username = "username";
        var trainer = new Trainer();
        trainer.setUsername(username);
        when(repository.findByUsername(username)).thenReturn(Optional.of(trainer));
        var actual = service.getByUsername(username);
        assertEquals(trainer, actual);
    }

    @Test
    public void testGetByUsername_withAbsentTrainer_throwException() {
        var username = "username";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getByUsername(username));
    }

    @Test
    public void testCreate() {
        var createDto = new CreateTrainerDto();
        createDto.setFirstName("Bob");
        createDto.setLastName("Doe");
        createDto.setSpecialization(1L);
        var trainee = mapper.toTrainer(createDto);
        when(repository.save(trainee)).thenReturn(trainee);
        var actual = service.create(createDto);
        assertEquals(trainee, actual);
        verify(userService, times(1)).prepareUserForCreation(trainee);
    }

    @Test
    public void testUpdate() {
        var updateDto = new UpdateTrainerDto();
        updateDto.setId(123L);
        updateDto.setSpecialization(2L);

        var type1 = new TrainingType(1L, "A");
        var type2 = new TrainingType(2L, "B");

        var foundTrainer = new Trainer();
        foundTrainer.setId(updateDto.getId());
        foundTrainer.setSpecialization(type1);

        var trainer = new Trainer();
        trainer.setId(updateDto.getId());
        trainer.setSpecialization(type2);

        when(repository.findById(updateDto.getId())).thenReturn(Optional.of(foundTrainer));
        when(repository.save(trainer)).thenReturn(trainer);
        when(trainingTypeService.getById(updateDto.getSpecialization())).thenReturn(type2);

        var actual = service.update(updateDto);
        assertEquals(trainer, actual);
        verify(userService, times(1)).applyUserUpdates(foundTrainer, updateDto);
    }

}

