package dev.branow.services;

import dev.branow.config.ValidationConfig;
import dev.branow.dtos.CreateTraineeDto;
import dev.branow.dtos.UpdateTraineeDto;
import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.mappers.TraineeMapper;
import dev.branow.model.Trainee;
import dev.branow.model.User;
import dev.branow.repositories.TraineeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static dev.branow.services.ValidationTest.testValidation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig({ ValidationConfig.class, TraineeService.class, TraineeMapper.class })
@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @MockitoBean
    private TraineeRepository repository;
    @MockitoBean
    private UserService userService;

    @Autowired
    private TraineeMapper mapper;
    @Autowired
    private TraineeService service;

    @Test
    public void testGetById_withPresentTrainee_returnTrainee() {
        var id = 123L;
        var trainee = new Trainee();
        trainee.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(trainee));
        var actual = service.getById(id);
        assertEquals(trainee, actual);
    }

    @Test
    public void testGetById_withAbsentTrainee_throwException() {
        var id = 123L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(id));
    }

    @Test
    public void testGetByUsername_withPresentTrainee_returnTrainee() {
        var username = "username";
        var trainee = new Trainee();
        trainee.setUsername(username);
        when(repository.findByUsername(username)).thenReturn(Optional.of(trainee));
        var actual = service.getByUsername(username);
        assertEquals(trainee, actual);
    }

    @Test
    public void testGetByUsername_withAbsentTrainee_throwException() {
        var username = "username";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getByUsername(username));
    }

    @Test
    public void testCreate() {
        var createDto = new CreateTraineeDto();
        createDto.setFirstName("Bob");
        createDto.setLastName("Doe");
        createDto.setAddress("address");
        createDto.setDateOfBirth(LocalDate.of(1960, 2, 4));
        var trainee = mapper.toTrainee(createDto);
        when(repository.save(trainee)).thenReturn(trainee);
        var actual = service.create(createDto);
        assertEquals(trainee, actual);
        verify(userService, times(1)).prepareUserForCreation(trainee);
    }

    @ParameterizedTest
    @MethodSource("provideTestCreate_CreateTraineeDtoValidation")
    public void testCreate_CreateTraineeDtoValidation(CreateTraineeDto dto, boolean isValid) {
        testValidation(isValid, () -> service.create(dto));
    }

    private static Stream<Arguments> provideTestCreate_CreateTraineeDtoValidation() {
        return Stream.of(
                Arguments.of(new CreateTraineeDto("ab", "cd", null, null), true),
                Arguments.of(new CreateTraineeDto("a".repeat(45), "b".repeat(45), null, null), true),
                Arguments.of(new CreateTraineeDto("a", "cd", null, null), false),
                Arguments.of(new CreateTraineeDto("ab", "c", null, null), false),
                Arguments.of(new CreateTraineeDto("a".repeat(46), "cd", null, null), false),
                Arguments.of(new CreateTraineeDto("ab", "c".repeat(46), null, null), false),
                Arguments.of(new CreateTraineeDto(null, "cd", null, null), false),
                Arguments.of(new CreateTraineeDto("ab", null, null, null), false),
                Arguments.of(new CreateTraineeDto("ab", "cd", LocalDate.of(2012, 12, 1), null), true),
                Arguments.of(new CreateTraineeDto("ab", "cd", null, ""), true),
                Arguments.of(new CreateTraineeDto("ab", "cd", null, "a".repeat(255)), true),
                Arguments.of(new CreateTraineeDto("ab", "cd", null, "a".repeat(256)), false)
        );
    }

    @Test
    public void testUpdate() {
        var updateDto = new UpdateTraineeDto();
        updateDto.setId(123L);
        updateDto.setFirstName("John");
        updateDto.setLastName("Bob");
        updateDto.setAddress("address");
        updateDto.setDateOfBirth(LocalDate.of(1960, 2, 4));

        var foundTrainee = new Trainee();
        foundTrainee.setId(updateDto.getId());
        foundTrainee.setAddress("address1");
        foundTrainee.setDateOfBirth(LocalDate.of(1990, 2, 4));

        var trainee = new Trainee();
        trainee.setId(updateDto.getId());
        trainee.setAddress(updateDto.getAddress());
        trainee.setDateOfBirth(updateDto.getDateOfBirth());

        when(repository.findById(updateDto.getId())).thenReturn(Optional.of(foundTrainee));
        when(repository.save(trainee)).thenReturn(trainee);

        var actual = service.update(updateDto);
        assertEquals(trainee, actual);
        verify(userService, times(1)).applyUserUpdates(foundTrainee, updateDto);
    }

    @Test
    public void testDeleteByUsername() {
        var username = "username";
        service.deleteByUsername(username);
        verify(repository, times(1)).deleteByUsername(username);
    }

}
