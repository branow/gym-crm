package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.CreateTraineeDto;
import dev.branow.dtos.TraineeDto;
import dev.branow.dtos.UpdateTraineeDto;
import dev.branow.mappers.TraineeMapper;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository repository;
    private final TraineeMapper mapper;
    private final UserService userService;
    private final TrainerRepository trainerRepository;

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Id.class)
    @Log("getting trainee by id %0")
    public TraineeDto getById(Long id) {
        return mapper.toTraineeDto(repository.getReferenceById(id));
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("getting trainee by username %0")
    public TraineeDto getByUsername(String username) {
        return mapper.toTraineeDto(repository.getReferenceByUsername(username));
    }

    @Log("creating trainee with %0")
    public TraineeDto create(@Valid CreateTraineeDto dto) {
        var trainee = mapper.toTrainee(dto);
        userService.prepareUserForCreation(trainee);
        var savedTrainee = repository.save(trainee);
        return mapper.toTraineeDto(savedTrainee);
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.UpdateTraineeDto.class)
    @Log("updating trainee with %0")
    public TraineeDto update(@Valid UpdateTraineeDto dto) {
        var trainee = repository.getReferenceById(dto.getId());
        userService.applyUserUpdates(trainee, dto);
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
        return mapper.toTraineeDto(trainee);
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("deleting trainee by username %0")
    public void deleteByUsername(String username) {
        repository.deleteByUsername(username);
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("adding new favorite trainer %1 for trainee %0")
    public TraineeDto addFavoriteTrainer(String traineeUsername, String trainerUsername) {
        var trainee = repository.getReferenceByUsername(traineeUsername);
        var trainer = trainerRepository.getReferenceByUsername(trainerUsername);
        trainee.getTrainers().add(trainer);
        return mapper.toTraineeDto(trainee);
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("adding new favorite trainer %1 for trainee %0")
    public TraineeDto deleteFavoriteTrainer(String traineeUsername, String trainerUsername) {
        var trainee = repository.getReferenceByUsername(traineeUsername);
        var trainer = trainerRepository.getReferenceByUsername(trainerUsername);
        trainee.getTrainers().remove(trainer);
        return mapper.toTraineeDto(trainee);
    }

}
