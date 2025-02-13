package dev.branow.services;

import dev.branow.DBTest;
import dev.branow.config.ValidationConfig;
import dev.branow.dtos.CreateTraineeDto;
import dev.branow.dtos.UpdateTraineeDto;
import dev.branow.mappers.TraineeMapper;
import dev.branow.mappers.TrainingMapper;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.model.Trainee;
import dev.branow.model.User;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.stream.Stream;

import static dev.branow.services.ValidationTest.testValidation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

@SpringJUnitConfig({
        ValidationConfig.class,
        TraineeService.class,
        TraineeMapper.class,
        TraineeRepository.class,
        TrainerRepository.class,
        TrainingMapper.class,
        TrainingTypeMapper.class,
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
    public void testGetById_withPresentTrainee_returnTraineeDto() {
        var id = 1L;
        var trainee = manager.find(Trainee.class, id);
        var traineeDto = mapper.toTraineeDto(trainee);
        var actual = service.getById(id);
        assertEquals(traineeDto, actual);
    }

    @Test
    public void testGetById_withAbsentTrainee_throwException() {
        assertThrows(ObjectNotFoundException.class, () -> service.getById(-1L));
    }

    @Test
    @Transactional
    public void testGetByUsername_withPresentTrainee_returnTrainee() {
        var id = 1L;
        var trainee = manager.find(Trainee.class, id);
        var traineeDto = mapper.toTraineeDto(trainee);
        var actual = service.getByUsername(trainee.getUsername());
        assertEquals(traineeDto, actual);
    }

    @Test
    public void testGetByUsername_withAbsentTrainee_throwException() {
        assertThrows(Exception.class, () -> service.getByUsername("xxxxxx"));
//        assertThrows(ObjectNotFoundException.class, () -> service.getByUsername("xxxxxx"));
    }

    @Test
    public void testCreate() {
        var username = "username";
        var passwrod = "passwrod";

        var createDto = new CreateTraineeDto();
        createDto.setFirstName("Bob");
        createDto.setLastName("Doe");
        createDto.setAddress("address");
        createDto.setDateOfBirth(LocalDate.of(1960, 2, 4));
        var trainee = mapper.toTrainee(createDto);

        var query = "select max(id) + 1 from users";
        Long expectedId = (Long) manager.createNativeQuery(query, Long.class).getResultList().getFirst();

        doAnswer(invocation -> {
           var user = (User) invocation.getArgument(0);
           user.setUsername(username);
           user.setPassword(passwrod);
           user.setIsActive(false);
           return null;
        }).when(userService).prepareUserForCreation(trainee);

        var actual = service.create(createDto);

        var traineeDto = mapper.toTraineeDto(trainee);
        traineeDto.setUsername(username);
        traineeDto.setId(expectedId);

        assertEquals(expectedId, actual.getId());
        assertEquals(traineeDto, actual);
    }

    @ParameterizedTest
    @MethodSource("provideTestCreate_CreateTraineeDtoValidation")
    public void testCreate_CreateTraineeDtoValidation(CreateTraineeDto dto, boolean isValid) {
        testValidation(isValid, () -> service.create(dto));
    }

    private static Stream<Arguments> provideTestCreate_CreateTraineeDtoValidation() {
        return Stream.of(
                Arguments.of(new CreateTraineeDto("a", "cd", null, null), false),
                Arguments.of(new CreateTraineeDto("ab", "c", null, null), false),
                Arguments.of(new CreateTraineeDto("a".repeat(46), "cd", null, null), false),
                Arguments.of(new CreateTraineeDto("ab", "c".repeat(46), null, null), false),
                Arguments.of(new CreateTraineeDto(null, "cd", null, null), false),
                Arguments.of(new CreateTraineeDto("ab", null, null, null), false),
                Arguments.of(new CreateTraineeDto("ab", "cd", null, "a".repeat(256)), false)
        );
    }

    @Test
    @Transactional
    public void testUpdate() {
        var id = 1L;
        var oldTrainee = repository.findById(id).get();

        var updateDto = new UpdateTraineeDto();
        updateDto.setId(id);
        updateDto.setFirstName(oldTrainee.getFirstName() + "1");
        updateDto.setLastName(oldTrainee.getLastName() + "1");
        updateDto.setAddress(oldTrainee.getAddress() + "1");
        updateDto.setDateOfBirth(LocalDate.of(1960, 2, 4));

        doAnswer(invocation -> {
            var user = (User) invocation.getArgument(0);
            var updateUserDto = (UpdateTraineeDto) invocation.getArgument(1);
            user.setFirstName(updateUserDto.getFirstName());
            user.setLastName(updateUserDto.getLastName());
            user.setUsername(updateUserDto.getFirstName() + "." + updateUserDto.getLastName());
            return null;
        }).when(userService).applyUserUpdates(oldTrainee, updateDto);

        var actual = service.update(updateDto);
        oldTrainee.setDateOfBirth(updateDto.getDateOfBirth());
        oldTrainee.setAddress(updateDto.getAddress());
        var expected = mapper.toTraineeDto(oldTrainee);
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteByUsername() {
        service.deleteByUsername("John.Doe");
        assertNull(manager.find(Trainee.class, 1L));
    }

    @Test
    public void testAddFavoriteTrainer() {
        var traineeId = 1L;
        var trainerId = 10L;
        service.addFavoriteTrainer("John.Doe", "Liam.Roberts");
        var query = String.format("select trainee_id from trainee_favorite_trainers where trainer_id = %d and trainee_id = %d", trainerId, traineeId);
        var actualTraineeId = (Long) manager.createNativeQuery(query).getResultList().getFirst();
        assertEquals(traineeId, actualTraineeId);
    }

    @Test
    public void testRemoveFavoriteTrainer() {
        Long traineeId = 1L, trainerId = 4L;
        service.deleteFavoriteTrainer("John.Doe", "Emma.Wilson");
        var query = String.format("select trainee_id from trainee_favorite_trainers where trainer_id = %d and trainee_id = %d", trainerId, traineeId);
        assertTrue(manager.createNativeQuery(query).getResultList().isEmpty());
    }

}
