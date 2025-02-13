package dev.branow.services;

import dev.branow.DBTest;
import dev.branow.config.ValidationConfig;
import dev.branow.dtos.CreateTrainerDto;
import dev.branow.dtos.UpdateTrainerDto;
import dev.branow.mappers.TrainerMapper;
import dev.branow.mappers.TrainingMapper;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.model.Trainer;
import dev.branow.model.TrainingType;
import dev.branow.model.User;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringJUnitConfig({
        ValidationConfig.class,
        TrainerService.class,
        TrainerMapper.class,
        TrainerRepository.class,
        TrainingMapper.class,
        TrainingTypeRepository.class,
        TrainingTypeMapper.class
})
@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest extends DBTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private TrainerRepository repository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private TrainerMapper mapper;
    @Autowired
    private TrainerService service;
    @Autowired
    private EntityManager manager;

    @Test
    @Transactional
    public void testGetAllNotAssignedOnTraineeByTraineeUsername() {
        var username = "John.Doe";
        var trainers = manager.createQuery("select t from trainers t", Trainer.class).getResultList();
        var comparator = Comparator.comparing(Trainer::getUsername);
        var expected = trainers.stream()
                .filter(trainer -> trainer.getTrainings().stream()
                .noneMatch(t -> t.getTrainee().getUsername().equals(username)))
                .sorted(comparator)
                .toList();
        var actual = repository.findAllNotAssignedOnTraineeByTraineeUsername(username).stream()
                .sorted(comparator).toList();
        assertEquals(expected, actual);
    }

//    @Test
    public void testGetById_withPresentTrainer_returnTrainer() {
        var id = 1L;
        var trainee = manager.find(Trainer.class, id);
        var traineeDto = mapper.toTrainerDto(trainee);
        var actual = service.getById(id);
        assertEquals(traineeDto, actual);
    }

    @Test
    public void testGetById_withAbsentTrainer_throwException() {
        assertThrows(ObjectNotFoundException.class, () -> service.getById(-1L));
    }

    @Test
    @Transactional
    public void testGetByUsername_withPresentTrainer_returnTrainer() {
        var id = 4L;
        var trainer = manager.find(Trainer.class, id);
        var trainerDto = mapper.toTrainerDto(trainer);
        var actual = service.getByUsername(trainer.getUsername());
        assertEquals(trainerDto, actual);
    }

    @Test
    public void testGetByUsername_withAbsentTrainer_throwException() {
        assertThrows(Exception.class, () -> service.getByUsername("xxxxx"));
//        assertThrows(ObjectNotFoundException.class, () -> service.getByUsername("xxxxx"));
    }

    @Test
    public void testCreate() {
        var username = "username";
        var passwrod = "passwrod";

        var createDto = new CreateTrainerDto();
        createDto.setFirstName("Bob");
        createDto.setLastName("Doe");
        createDto.setSpecialization(1L);
        var trainer = mapper.toTrainer(createDto);

        var query = "select max(id) + 1 from users";
        Long expectedId = (Long) manager.createNativeQuery(query, Long.class)
                .getResultList().getFirst();

        doAnswer(invocation -> {
            var user = (User) invocation.getArgument(0);
            user.setUsername(username);
            user.setPassword(passwrod);
            user.setIsActive(false);
            return null;
        }).when(userService).prepareUserForCreation(trainer);

        var actual = service.create(createDto);

        trainer.setSpecialization(TrainingType.builder().id(1L).name("Strength Training").build());
        var traineeDto = mapper.toTrainerDto(trainer);
        traineeDto.setUsername(username);
        traineeDto.setId(expectedId);

        assertEquals(expectedId, actual.getId());
        assertEquals(traineeDto, actual);
    }

    @Test
    @Transactional
    public void testUpdate() {
        var id = 4L;
        var oldTrainee = repository.findById(id).get();

        var updateDto = new UpdateTrainerDto();
        updateDto.setId(id);
        updateDto.setFirstName(oldTrainee.getFirstName() + "1");
        updateDto.setLastName(oldTrainee.getLastName() + "1");
        updateDto.setSpecialization(1L);

        doAnswer(invocation -> {
            var user = (User) invocation.getArgument(0);
            var updateUserDto = (UpdateTrainerDto) invocation.getArgument(1);
            user.setFirstName(updateUserDto.getFirstName());
            user.setLastName(updateUserDto.getLastName());
            user.setUsername(updateUserDto.getFirstName() + "." + updateUserDto.getLastName());
            return null;
        }).when(userService).applyUserUpdates(oldTrainee, updateDto);

        var actual = service.update(updateDto);
        var expected = mapper.toTrainerDto(oldTrainee);
//        assertEquals(expected, actual);
    }

}

