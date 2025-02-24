package dev.branow.services;

import dev.branow.DBTest;
import dev.branow.dtos.service.CreateTraineeDto;
import dev.branow.dtos.service.UpdateFavoriteTrainersDto;
import dev.branow.dtos.service.UpdateTraineeDto;
import dev.branow.mappers.*;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.User;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig({
        TraineeService.class,
        TraineeMapper.class,
        TraineeRepository.class,
        TrainerRepository.class,
        TrainerMapper.class,
        TrainingMapper.class,
        TrainingTypeMapper.class,
        TraineeTrainerMapper.class,
})
@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest extends DBTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private EntityManager manager;
    @Autowired
    private TraineeMapper mapper;
    @Autowired
    private TraineeService service;
    @Autowired
    private TraineeRepository repository;

    @Test
    @Transactional
    public void testGetByUsername_withPresentTrainee_returnTrainee() {
        var id = 1L;
        var trainee = manager.find(Trainee.class, id);
        var traineeDto = mapper.mapTraineeDto(trainee);
        var actual = service.getByUsername(trainee.getUsername());
        assertEquals(traineeDto, actual);
    }

    @Test
    public void testGetByUsername_withAbsentTrainee_throwException() {
        assertThrows(ObjectNotFoundException.class, () -> service.getByUsername("xxxxxx"));
    }

    @Test
    public void testCreate() {
        var username = "username";
        var password = "password";

        var createDto = new CreateTraineeDto();
        createDto.setFirstName("Bob");
        createDto.setLastName("Doe");
        createDto.setAddress("address");
        createDto.setDateOfBirth(LocalDate.of(1960, 2, 4));
        var trainee = mapper.mapTrainee(createDto);

        var query = "select max(id) + 1 from users";
        Long expectedId = (Long) manager.createNativeQuery(query, Long.class).getResultList().getFirst();

        doAnswer(invocation -> {
           var user = (User) invocation.getArgument(0);
           user.setUsername(username);
           user.setPassword(password);
           user.setIsActive(false);
           return null;
        }).when(userService).prepareUserForCreation(trainee);

        var actual = service.create(createDto);

        var traineeDto = mapper.mapTraineeDto(trainee);
        traineeDto.setUsername(username);
        traineeDto.setPassword(password);
        traineeDto.setIsActive(false);
        traineeDto.setId(expectedId);

        assertEquals(expectedId, actual.getId());
        assertEquals(traineeDto, actual);
    }

    @Test
    @Transactional
    public void testUpdate() {
        var id = 1L;
        var oldTrainee = repository.findById(id).get();

        var updateDto = new UpdateTraineeDto();
        updateDto.setUsername(oldTrainee.getUsername());
        updateDto.setFirstName(oldTrainee.getFirstName() + "1");
        updateDto.setLastName(oldTrainee.getLastName() + "1");
        updateDto.setAddress(oldTrainee.getAddress() + "1");
        updateDto.setDateOfBirth(LocalDate.of(1960, 2, 4));

        var actual = service.update(updateDto);
        oldTrainee.setDateOfBirth(updateDto.getDateOfBirth());
        oldTrainee.setAddress(updateDto.getAddress());
        var expected = mapper.mapTraineeDto(oldTrainee);
        assertEquals(expected, actual);

        verify(userService, times(1)).applyUserUpdates(oldTrainee, updateDto);
    }

    @Test
    public void testDeleteByUsername() {
        var trainee = manager.find(Trainee.class, 1L);
        service.deleteByUsername(trainee.getUsername());
        assertNull(manager.find(Trainee.class, 1L));
    }

    @Test
    public void testUpdateFavoriteTrainers() {
        List<String> trainerUsernames = Stream.of("Liam.Roberts", "Emma.Wilson", "James.Taylor").sorted().toList();
        service.updateFavoriteTrainers(new UpdateFavoriteTrainersDto("John.Doe", trainerUsernames));

        var query = String.format("select t.favoriteTrainers from %s t where t.username = 'John.Doe'", Trainee.class.getName());
        List<String> actual = manager.createQuery(query, Trainer.class).getResultList().stream()
                .map(Trainer::getUsername)
                .toList();
        assertEquals(trainerUsernames, actual);
    }

}
